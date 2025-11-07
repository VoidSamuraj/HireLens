package com.voidsamuraj.HireLens.service.ai;

import com.voidsamuraj.HireLens.dto.ai.AnalysisResult;
import com.voidsamuraj.HireLens.dto.ai.JobOfferRequest;
import com.voidsamuraj.HireLens.dto.ai.SkillsRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for communicating with an external AI system to analyze job offers
 * and group skills.
 * <p>
 * Uses Spring WebClient to perform HTTP POST requests to the AI service.
 * Methods are blocking (synchronous) and include retry logic in case of transient failures.
 * </p>
 *
 * <p>Endpoints used:</p>
 * <ul>
 *   <li>{@code POST /analyze} – analyzes the text of a job offer and returns a structured
 *       {@link AnalysisResult}, including detected seniority and skills.</li>
 *   <li>{@code POST /groupSkills} – groups a flat map of skills into structured categories
 *       with aggregated levels.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiClientService {

    /** Base address of the AI service. */
    private final String address;

    /** WebClient instance used to communicate with the AI service. Initialized in {@link #init()}. */
    private WebClient webClient;

    /**
     * Initializes the {@link WebClient} with the configured base URL.
     * Invoked after dependency injection.
     */
    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(address)
                .build();
    }

    /**
     * Sends a job text to the AI service for analysis.
     * <p>
     * Returns structured {@link AnalysisResult} containing detected seniority level
     * and skill categories with their levels.
     * Retries up to 5 times with a 3-second delay in case of failure.
     * </p>
     *
     * @param jobText raw text of the job offer
     * @return {@link AnalysisResult} with seniority and skills
     * @throws RuntimeException if the AI service is unavailable or an error occurs
     */
    public AnalysisResult analyzeJob(String jobText) {
        try {
            return webClient.post()
                    .uri("/analyze")
                    .bodyValue(new JobOfferRequest(jobText))
                    .retrieve()
                    .bodyToMono(AnalysisResult.class)
                    .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(3)))
                    .block(); // blocking, not reactive
        } catch (Exception e) {
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a list of skills to the AI service to be grouped into categories.
     * <p>
     * Returns a nested map where the top-level keys are skill and values
     * are categories
     * Retries up to 5 times with a 3-second delay in case of failure.
     * <p>
     * Logs warnings if the AI service returns {@code null} or an empty map.
     * </p>
     *
     * @param skills list of skills to group
     * @return map of grouped skills (skill -> category)
     * @throws RuntimeException if the AI service is unavailable or an error occurs
     */
    public Map<String, String> groupSkills(List<String> skills){
        try {
            Map<String, String> result = webClient.post()
                    .uri("/groupSkills")
                    .bodyValue(new SkillsRequest(skills))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                    .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(3)))
                    .block(); // blocking, not reactive

            // --- verification of deserialization ---
            if (result == null) {
                log.warn("AI service returned null result for skills grouping");
                return new HashMap<>();
            }
            if (result.isEmpty()) {
                log.warn("AI service returned empty or unparsable grouped map: {}", result);
                return new HashMap<>();
            }else{
                log.info("AI service returned map: {}", result);
            }

            // --- optional success logging ---
            log.debug("AI service grouping success, received {} categories", result.size());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("AI service unavailable: " + e.getMessage(), e);
        }
    }
}
