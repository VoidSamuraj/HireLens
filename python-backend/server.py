"""
Job Offer Analyzer API
----------------------

This FastAPI application analyzes job descriptions and extracts:
- Seniority level (intern, junior, mid, senior)
- Technical skills and their importance levels (1–5)
- Deduplicated and merged skill names based on semantic similarity

It uses:
- HuggingFace transformers for NER and LLM
- SentenceTransformers for skill embeddings
- FastAPI + Pydantic for the API interface
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Dict
import os
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM, pipeline
from huggingface_hub import login
import re
import json
from sentence_transformers import SentenceTransformer, util
from functools import lru_cache
import itertools
import asyncio
from concurrent.futures import ThreadPoolExecutor

executor = ThreadPoolExecutor(max_workers=1)  # 1 LLM worker
# -----------------------------
# FastAPI initialization
# -----------------------------
app = FastAPI(title="Job Offer Analyzer", version="1.0")

# -----------------------------
# Environment setup
# -----------------------------
os.environ["HF_HOME"] = "/models"

use_cuda = os.environ.get("USE_CUDA", "false").lower() == "true"
device = "cuda" if use_cuda and torch.cuda.is_available() else "cpu"
device_id = 0 if (use_cuda and torch.cuda.is_available()) else -1

# HuggingFace authentication
hf_token = os.getenv("HF_TOKEN")
if hf_token is None:
    raise RuntimeError("HF_TOKEN not set in environment")
login(token=hf_token)

# -----------------------------
# Models
# -----------------------------
# Named Entity Recognition model (skills extraction)
ner_model_name = "yashpwr/resume-ner-bert-v2"
ner_classifier = pipeline(
    "ner",
    model=ner_model_name,
    aggregation_strategy="simple",
    device=device_id
)

# Large Language Model (LLM) for reasoning
llm_model_name = "NousResearch/Hermes-3-Llama-3.2-3B"
tokenizer = AutoTokenizer.from_pretrained(llm_model_name)
tokenizer.pad_token = tokenizer.eos_token
llm_model = AutoModelForCausalLM.from_pretrained(
    llm_model_name,
    dtype=torch.float16 if device == "cuda" else torch.float32,
    device_map={"": device}
)

# Sentence embeddings for skill similarity
embedding_model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")

# -----------------------------
# Technical skill filter (semantic)
# -----------------------------
TECH_EXAMPLES = [
    # --- Programming languages ---
    "Java", "Python", "C++", "C#", "JavaScript", "TypeScript", "Go", "Rust",
    "Kotlin", "Swift", "PHP", "Ruby", "Scala", "Perl",
    # --- Backend frameworks ---
    "Spring Boot", "Spring", "Node.js", "Express.js", "NestJS", "Django",
    "Flask", "FastAPI", "Laravel", "ASP.NET Core", "GraphQL",
    # --- Frontend ---
    "React", "Next.js", "Angular", "Vue.js", "Svelte", "Redux",
    "Tailwind CSS", "Bootstrap", "Material UI",
    # --- Cloud & DevOps ---
    "AWS", "Azure", "Google Cloud", "Docker", "Kubernetes", "Terraform",
    "Ansible", "Jenkins", "GitHub Actions", "CI/CD", "Serverless",
    # --- Databases ---
    "PostgreSQL", "MySQL", "MongoDB", "Redis", "Elasticsearch", "Kafka",
    "Oracle", "SQLite", "Cassandra", "DynamoDB", "Neo4j",
    # --- AI / ML / Data ---
    "TensorFlow", "PyTorch", "scikit-learn", "XGBoost", "pandas", "NumPy",
    "HuggingFace", "LangChain", "Data Science", "Machine Learning",
    # --- Testing ---
    "JUnit", "Selenium", "Cypress", "Playwright", "TestNG", "Appium",
    "Espresso", "Mockito", "Postman",
    # --- Mobile ---
    "Android", "iOS", "Jetpack Compose", "React Native", "Flutter",
    # --- Misc ---
    "Microservices", "API Gateway", "DevOps", "Backend Development",
    "Frontend Development", "Full Stack Development"
]
SUPPORT_EXAMPLES = [
    # --- Process & tools ---
    "Agile", "Scrum", "Kanban", "CI/CD", "Version Control", "Git",
    "Test Automation", "Unit Testing", "Integration Testing",
    "TDD", "BDD", "SOLID Principles", "Design Patterns",
    "DevOps", "Microservices", "System Architecture",
    "Service Oriented Architecture", "API Design", "REST", "GraphQL",
    "Monitoring", "Logging", "Continuous Deployment",
    "Performance Optimization", "Security Best Practices",
]
NON_TECH_PHRASES = {
    "developer", "engineer", "lead", "specialist", "expert", "intern", "manager",
    "architect", "consultant", "analyst", "technician", "administrator",

    "environment", "methodologies", "challenges", "solutions", "projects", "roles",
    "package", "benefits", "bonus", "hybrid", "type", "location", "variable",
    "salary", "contract", "permanent", "opportunity", "career", "experience",

    "collaboration", "team player", "communication", "fast-paced", "problem solving",
    "independent", "adaptable", "motivated", "responsibility", "leadership",

    "appropriate", "cutting-edge", "large-scale", "extensive", "real-world", "client-facing",
    "innovative", "scalable", "dynamic", "robust", "world-class",

    "performance", "high performance", "design solutions", "solution development",
    "maximize efficiency", "optimization", "secure", "security focus",
    "life insurance", "life insurance systems", "business dashboards",
    "software development", "development services", "patterns",
    "agile mindset", "agile environment", "agile methodologies",
    "fluent english", "spoken english", "written english", "language skills"
}
too_generic = {
    "development", "software", "software development",
    "system", "systems", "application", "applications",
    "backend", "back-end", "front-end", "frontend",
    "programming", "coding", "design", "architecture",
    "cloud", "database", "databases", "api", "apis"
}

tech_embeddings = embedding_model.encode(TECH_EXAMPLES, convert_to_tensor=True)
support_embeddings = embedding_model.encode(SUPPORT_EXAMPLES, convert_to_tensor=True)

# -----------------------------
# Data models
# -----------------------------

class AnalysisResult(BaseModel):
    """The result of analyzing a job offer: seniority + list of skills."""
    seniority: str
    skills:  Dict[str, int]

class JobOffer(BaseModel):
    """Input model representing a job offer text to analyze."""
    text: str

class SkillsRequest(BaseModel):
    """Input model representing a skills to group."""
    skills: List[str]

# -----------------------------
# Utility functions
# -----------------------------
def extract_skills(text: str) -> List[str]:
    """Extract candidate skills using the NER pipeline."""
    ner_results = ner_classifier(text)
    skills = [
        ent["word"].strip()
        for ent in ner_results
        if ent["entity_group"] not in {"ORG", "LOC", "PER"}
    ]
    return list(dict.fromkeys(skills))  # deduplicate


def query_llm(prompt: str, max_new_tokens: int = 200) -> str:
    """Send a prompt to the LLM and return the generated text."""
    try:
        current_device = next(llm_model.parameters()).device
        if current_device.type != "cuda" and torch.cuda.is_available():
            print("⚠️ Model don't work with GPU — changing to GPU...")
            llm_model.to("cuda")
            torch.cuda.empty_cache()
        inputs = tokenizer(prompt, return_tensors="pt").to(device)
        with torch.no_grad():
            outputs = llm_model.generate(**inputs, max_new_tokens=max_new_tokens)
        result = tokenizer.decode(outputs[0], skip_special_tokens=True).strip()
        parts = result.split("OUTPUT:")
        return parts[1].strip() if len(parts) > 1 else result
    except Exception as e:
        print("DEBUG: LLM query failed:", str(e))
        return ""

def ask_llm_for_seniority(text: str) -> str:
    """
    Determine seniority level (intern, junior, mid, senior) from a job description.
    Defaults to 'mid' if uncertain.
    """
    try:
        prompt = f"""
        Analyze the following job description and determine the seniority of the role.
        Return EXACTLY ONE WORD from: ["junior", "mid", "senior", "intern"].
        Do NOT add any explanation, context, or punctuation—only the word.

        Pay special attention to words that indicate entry-level positions,
        internships, or traineeships.

        Job description:
        {text.lower()}

        OUTPUT:
        """
        allowed = {"junior", "mid", "senior", "intern"}
        words = query_llm(prompt, max_new_tokens=200).strip().lower()[:20]

        return min((w for w in allowed if w in words),
                   key=lambda w: words.index(w),
                   default="mid")
    except Exception as e:
        print("DEBUG: Seniority detection failed:", str(e))
        return "mid"

def ask_llm_for_skills(text: str) -> List[str]:
    """Extract technical skills from the job description using LLM JSON output."""
    prompt = f"""
    Extract all technical skills, programming languages, frameworks, tools,
    and libraries mentioned in the following job description.
    Return ONLY a JSON array of strings (skills), nothing else.
    The output must start with '{{' and end with '}}'.

    Job description:
    {text}

    JSON OUTPUT:
    """
    result = query_llm(prompt, max_new_tokens=200)
    try:
        matches = re.findall(r"\[.*?\]", result, re.DOTALL)
        for m in matches:
            try:
                skills = json.loads(m)
                filtered_skills = [s.strip() for s in skills if len(s.strip().split()) <= 3]
                return filtered_skills
            except json.JSONDecodeError:
                continue
        raise ValueError("No valid JSON array found in model output")
    except Exception as e:
        print("DEBUG: JSON parse failed for skills:", str(e))
        return []

def ask_llm_for_skill_levels(text: str, skills: List[str]) -> Dict[str, int]:
    """Assign an importance level (1–5) to each skill."""
    prompt = f"""
    Assign a level 1–5 to each skill in the job description.
    Levels:
      5 = must-have / expert
      4 = required / strong experience
      3 = mid / some familiarity
      2 = good-to-have
      1 = optional / very minor

    Return ONLY JSON: skill -> level.
    The output must start with '{{' and end with '}}'.

    Job description:
    {text}

    Skills: {', '.join(skills)}

    JSON OUTPUT:
    """
    result = query_llm(prompt, max_new_tokens=200)
    try:
        matches = re.findall(r"\{.*?\}", result, re.DOTALL)
        for m in matches:
            try:
                skill_map = json.loads(m)
                return {k: int(v) for k, v in skill_map.items()}
            except json.JSONDecodeError:
                continue
        raise ValueError("No valid JSON object found in model output")
    except Exception as e:
        print("DEBUG: JSON parse failed:", str(e))
        return {k: 5 for k in skills}  # fallback: assume all are must-have

def is_probably_technical(skill: str, threshold: float = 0.55, support_bonus: float = 0.05) -> bool:
    """
    Checks if a skill is technical or semi-technical (support).
    Gives priority to tech examples but allows support if close enough.
    """

    s = skill.lower().strip()
    if len(s.split()) > 2:
        return False

    if any(bad in s for bad in NON_TECH_PHRASES):
        return False

    if s in too_generic:
        return False

    try:
        emb = get_skill_embedding(skill)
        tech_sim = torch.max(util.cos_sim(emb, tech_embeddings)).item()
        support_sim = torch.max(util.cos_sim(emb, support_embeddings)).item()

        # Główny warunek: tech powyżej progu
        if tech_sim >= threshold:
            return True

        # Warunek wspierający: support blisko progu (np. Agile, Scrum)
        if support_sim >= (threshold + support_bonus / 2) and support_sim > tech_sim - support_bonus:
            return True

        return False

    except Exception as e:
        print(f"DEBUG: is_probably_technical failed for '{skill}':", str(e))
        return False
@lru_cache(maxsize=5000)
def get_skill_embedding(skill: str):
    """Cache and return sentence embedding for a skill string."""
    return embedding_model.encode(skill, convert_to_tensor=True)

def merge_similar_skills_with_levels(skill_levels: Dict[str, int],
                                     threshold: float = 0.75) -> Dict[str, int]:
    """
    Merge semantically similar skills (cosine similarity >= threshold).
    Keeps the highest level and the shortest (most canonical) name.
    """
    try:
        skills = list(skill_levels.keys())
        embeddings = [get_skill_embedding(s) for s in skills]
        merged, used = {}, set()

        for i, skill in enumerate(skills):
            if skill in used:
                continue

            group = [(skill, skill_levels[skill])]
            for j in range(i + 1, len(skills)):
                if skills[j] in used:
                    continue

                sim = util.cos_sim(embeddings[i], embeddings[j]).item()
                if sim >= threshold:
                    group.append((skills[j], skill_levels[skills[j]]))
                    used.add(skills[j])

            # Wybierz nazwę najkrótszą (np. "Spring" zamiast "Spring Boot")
            canonical = min(group, key=lambda x: len(x[0]))[0]
            level = max(l for _, l in group)
            merged[canonical] = level
            used.add(skill)

        return merged
    except Exception as e:
        print("DEBUG: Skill merge failed:", str(e))
        return skill_levels

def chunk_dict(d: dict, size: int = 50):
    """Split a dictionary into chunks of given size."""
    it = iter(d.items())
    for _ in range(0, len(d), size):
        yield dict(itertools.islice(it, size))

def group_skills_with_llm(skill_list: list[str], chunk_size: int = 50) -> dict[str, str]:
    """
    Group technical skills into top-level categories using LLM in chunks.

    Parameters:
    - skill_list: list of skill names (strings)
    - chunk_size: how many skills to send to LLM at once

    Returns:
    - dict: Map[Skill, Category]
    """
    all_grouped: dict[str, str] = {}

    # Podział listy na chunk'i
    for i in range(0, len(skill_list), chunk_size):
        chunk = skill_list[i:i+chunk_size]
        skill_text = json.dumps(chunk, indent=2)

        prompt = f"""
        You are an expert software engineer. Your task is to assign each skill to one of the following top-level categories:
        'Backend', 'Frontend', 'Fullstack', 'Mobile', 'DevOps', 'Cloud', 'Data/ML', 'Databases',
        'Testing', 'Security', 'Languages', 'Frameworks', 'Tools', 'Other'.

        Requirements:
        - Do NOT duplicate the same skill in multiple categories.
        - Respond with ONLY a JSON object mapping skill -> category.
        - Start output with '{{' and end with '}}'.

        Skills:
        {skill_text}

        JSON OUTPUT:
        """

        group_json = extract_first_json(query_llm(prompt, max_new_tokens=1500))

        try:
            parsed: dict = json.loads(group_json)
            for skill, category in parsed.items():
                all_grouped[skill] = category
        except Exception as e:
            print("DEBUG: JSON parse failed for chunk:", e)
            # fallback: przypisz wszystkie do "Other"
            for skill in chunk:
                all_grouped[skill] = "Other"

    return all_grouped




# def group_skills_with_llm(skill_map: dict, chunk_size: int = 50) -> dict:
#     """
#     Group technical skills into hierarchical categories using LLM in chunks.
#
#     Parameters:
#     - skill_map: dict of skill -> integer level
#     - chunk_size: how many skills to send to LLM at once
#
#     Returns:
#     - dict: Map<Category, Map<Skill, Level>>
#     """
#     all_grouped = {}
#
#     # Chunk the skills to avoid prompt overflow
#     for chunk in chunk_dict(skill_map, size=chunk_size):
#         skill_text = json.dumps(chunk, indent=2)
#
#         prompt = f"""
#         You are an expert software engineer. Your task is to group the following technical skills into **detailed, hierarchical categories**.
#         Think like a resume/skills analyst.
#
#         - Top-level, use only these categories: 'Backend', 'Frontend', 'Fullstack', 'Mobile', 'DevOps', 'Cloud', 'Data/ML', 'Databases', 'Testing', 'Security', 'Languages', 'Frameworks', 'Tools', 'Other'.
#         - Under each category, put subskills as key-value pairs, where the key is the skill name and the value is its integer level.
#
#         Requirements:
#         - Do NOT duplicate the same skill in multiple categories.
#         - Keep structure compact but meaningful.
#         - For skills that do not fit any existing category, create a new top-level category.
#         - Respond with ONLY a single valid JSON object (Map<String, Map<String, Integer>>).
#         - Start output with '{{' and end with '}}'.
#         - Use the integer values from the input; do not invent values.
#
#         Skills:
#         {skill_text}
#
#         JSON OUTPUT:
#         """
#         print("group llm")
#         # Call the LLM
#         group_json = extract_first_json(query_llm(prompt, max_new_tokens=1500))
#
#         print("make sure llm")
#         # Validate and clean JSON
#         grouped_chunk = make_sure_group_format_is_valid(group_json, chunk)
#
#         # Merge with overall result
#         for cat, subs in grouped_chunk.items():
#             if cat not in all_grouped:
#                 all_grouped[cat] = {}
#             for skill, level in subs.items():
#                 # Sum levels if duplicate
#                 all_grouped[cat][skill] = all_grouped[cat].get(skill, 0) + level
#
#     return all_grouped

# def group_skills_with_llm(skill_map: dict) -> str:
#     skill_text = json.dumps(skill_map, indent=2)
#     prompt = f"""
#     You are an expert software engineer.
#     Group the following technical skills into meaningful hierarchical categories.
#     Each category (like 'Web', 'Backend', 'DevOps', 'Mobile', 'Data', etc.)
#     should contain its relevant subskills as JSON key-value pairs (skill: level).
#
#     Requirements:
#     - Do NOT duplicate the same skill in multiple groups.
#     - Keep structure compact and logical.
#     - Respond with ONLY a single valid JSON object.
#     - Do NOT include any explanations, text, markdown, or code fences.
#     - The output must start with '{{' and end with '}}'.
#     - Use the integer values provided in the input. Do not repeat the skill name as the value. The value must be the integer level.
#     - The output JSON structure must be of type Map<String, Map<String, Integer>>.
#
#     Skills:
#     {skill_text}
#
#     JSON OUTPUT:
#     """
#     prompt = f"""
#     You are an expert software engineer. Your task is to group the following technical skills into **detailed, hierarchical categories**.
#     Think like a resume/skills analyst.
#
#     - Top-level categories can include: 'Backend', 'Frontend', 'Fullstack', 'Mobile', 'DevOps', 'Cloud', 'Data/ML', 'Databases', 'Testing', 'Security', 'Languages', 'Frameworks', 'Tools', 'Other'.
#     - Under each category, put **subskills as key-value pairs**, where the key is the skill name and the value is its integer level.
#
#     Requirements:
#     - Do NOT duplicate the same skill in multiple categories.
#     - Keep structure compact but meaningful.
#     - For skills that do not fit any existing category, create a new top-level category.
#     - Respond with ONLY a **single valid JSON object** (Map<String, Map<String, Integer>>).
#     - Do NOT include any explanations, text, markdown, or code fences.
#     - Start output with '{{' and end with '}}'.
#     - Use the integer values from the input; do not invent values.
#
#     Skills:
#     {skill_text}
#
#     JSON OUTPUT:
#     """
#     group = extract_first_json(query_llm(prompt, max_new_tokens=1000))
#     print("DEBUG: Original LLM result:\n", group)
#
#     result = make_sure_group_format_is_valid(group, skill_map)
#     print("DEBUG: JSON AFTER ASURING:\n", result)
#     return result



def make_sure_group_format_is_valid(group: str, skill_map: dict) -> dict:
    """
    Fix the input JSON to map<string, map<string,int>> using LLM.
    """

    prompt = f"""
    You are an expert software engineer.
    You receive a JSON object containing technical skills in arbitrary structure.
    Convert it to a clean JSON object with structure:

    Map<string, Map<string,int>>

    - Top-level keys are categories.
    - Subkeys are skills, values are integers (skill levels).
    - Flatten nested structures.
    - Use original skill_map to assign values if original is not numeric.
    - Return only valid JSON.

    Input JSON:
    {group}

    JSON OUTPUT:
    """

    result = extract_first_json(query_llm(prompt, max_new_tokens=1000))

    grouped = {}
    try:
        parsed = json.loads(result)

        for cat, subs in parsed.items():
            grouped[cat] = {}
            if isinstance(subs, dict):
                for k, v in subs.items():
                    try:
                        grouped[cat][k] = int(v)
                    except:
                        grouped[cat][k] = skill_map.get(k, 5)
            else:
                # if value is not dict, fallback
                grouped[cat][cat] = skill_map.get(cat, 5)
    except Exception as e:
        print("DEBUG: JSON parse failed. Fallback to single-level map")
        print("Error:", e)
        print("Bad Data:", result)
        grouped = {"Ungrouped": skill_map}

    return grouped

def extract_first_json(text: str) -> str:
    """
    Extracts the first JSON block from a string.
    - Tracks opening and closing braces.
    - If braces are balanced, returns full JSON.
    - If not, returns everything up to the last encountered '}'.
    Returns '{}' if no opening brace found.
    """
    start = text.find("{")
    if start == -1:
        return "{}"

    brace_count = 0
    last_closing_index = -1

    for i in range(start, len(text)):
        if text[i] == "{":
            brace_count += 1
        elif text[i] == "}":
            brace_count -= 1
            last_closing_index = i
            if brace_count == 0:
                return text[start:i+1]  # balanced JSON found

    # if braces are unbalanced, return up to last closing brace
    if last_closing_index != -1:
        return text[start:last_closing_index+1]
    # if no closing brace at all
    return text[start:]


# -----------------------------
# Middleware
# -----------------------------

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# -----------------------------
# API endpoint
# -----------------------------
# @app.post("/analyze")
# async def analyze(offer: JobOffer) -> AnalysisResult:
#     """
#     Analyze a job offer text and return:
#     - Seniority level (intern/junior/mid/senior)
#     - List of skills with levels (1–5)
#     """
#     loop = asyncio.get_event_loop()
#     seniority = await loop.run_in_executor(executor, ask_llm_for_seniority, offer.text)
#     skills_list = await loop.run_in_executor(executor, ask_llm_for_skills, offer.text)
#     skills_list = [s for s in skills_list if is_probably_technical(s)]
#     skill_levels = await loop.run_in_executor(executor, ask_llm_for_skill_levels, offer.text, skills_list)
#     skill_levels = merge_similar_skills_with_levels(skill_levels, threshold=0.75)
#     torch.cuda.synchronize()
#     torch.cuda.empty_cache()
#     return AnalysisResult(seniority=seniority, skills=skill_levels)


@app.post("/analyze")
async def analyze(offer: JobOffer) -> AnalysisResult:
    """
    Analyze a job offer text and return:
    - Seniority level (intern/junior/mid/senior)
    - List of skills with levels (1–5)
    """
    loop = asyncio.get_event_loop()
    seniority = await loop.run_in_executor(executor, ask_llm_for_seniority, offer.text)
    skills_list = await loop.run_in_executor(executor, ask_llm_for_skills, offer.text)
    skills_list = [s for s in skills_list if is_probably_technical(s)]
    skill_levels = await loop.run_in_executor(executor, ask_llm_for_skill_levels, offer.text, skills_list)
    skill_levels = merge_similar_skills_with_levels(skill_levels, threshold=0.75)
    skills = {k: v for k, v in skill_levels.items()}
#     grouped_skills =  await loop.run_in_executor(executor, group_skills_with_llm, skills)
#     print("DEBUG: analyze :\n", grouped_skills)
    return AnalysisResult(seniority=seniority, skills=skills)

@app.post("/groupSkills")
def group_skills_endpoint(req: SkillsRequest)-> Dict[str, str]:
    grouped = group_skills_with_llm(req.skills)
    return grouped