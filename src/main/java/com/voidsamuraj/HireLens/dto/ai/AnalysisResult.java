package com.voidsamuraj.HireLens.dto.ai;

import lombok.Data;

import java.util.Map;

/**
 * Represents the result of analyzing a job offer.
 * <p>
 * Stores information about the detected job seniority and structured skills.
 * </p>
 *
 * <p>Fields:</p>
 * <ul>
 *   <li><b>seniority</b>: the detected seniority level (e.g., "Junior", "Mid", "Senior").</li>
 *   <li><b>skills</b>: a structured map where each skill maps to corresponding level.</li>
 * </ul>
 *
 * <p>Example structure of the <code>skills</code> map:</p>
 * <pre>
 * {
 *     "Java": 5,
 *     "Python": 4
 *     "PostgreSQL": 3,
 *     "MongoDB": 2
 * }
 * </pre>
 *
 * <p>Example usage:</p>
 * <pre>
 * AnalysisResult result = new AnalysisResult();
 * result.setSeniority("Senior");
 * result.setSkills(Map.of("Java", 5, "Python", 4,"PostgreSQL", 3, "MongoDB", 2));
 * </pre>
 */
@Data
public class AnalysisResult {
    /** Detected job seniority level. */
    private String seniority;

    /** Map of skill to level. */
    private Map<String, Integer> skills;
}
