package com.voidsamuraj.HireLens.dto.ai;

import lombok.Data;

import java.util.Map;

/**
 * Represents the result of grouping skills.
 * Each skill is mapped to its importance level (integer).
 */
@Data
public class GroupResult {

    /**
     * Map of skill name to its integer level.
     * Example: "Python" -> 5, "Docker" -> 4
     */
    private Map<String, Integer> skills;
}