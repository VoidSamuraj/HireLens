package com.voidsamuraj.HireLens.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for sending a set of skills.
 * <p>
 * Typically used to submit skills for analysis, aggregation, or AI processing.
 * </p>
 *
 * <p>Example JSON structure:</p>
 * <pre>
 * {
 *   "skills": {
 *     "Java",
 *     "Python",
 *     "Spring"
 *   }
 * }
 * </pre>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillsRequest {

    /** Map of skill names to their corresponding levels or weights. */
    private List<String> skills;
}
