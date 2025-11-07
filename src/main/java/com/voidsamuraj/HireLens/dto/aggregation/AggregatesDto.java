package com.voidsamuraj.HireLens.dto.aggregation;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
/**
 * Data Transfer Object (DTO) representing aggregated statistics.
 * <p>
 * Contains summaries of locations and skills with their respective counts.
 * </p>
 * <p>
 * Example data:
 * <pre>
 * AggregatesDto aggregates = new AggregatesDto();
 *
 * // locations map: name -> occurrence count
 * locations = {
 *     "New York" -> 5,
 *     "London" -> 3,
 *     "Berlin" -> 2
 * };
 *
 * // skills map: group -> (skill name -> weight/count)
 * skills = {
 *     "backend" -> {
 *         "spring" -> 8,
 *         "hibernate" -> 4
 *     },
 *     "frontend" -> {
 *         "react" -> 6,
 *         "vue" -> 2
 *     },
 *     "devops" -> {
 *         "docker" -> 5,
 *         "kubernetes" -> 3
 *     }
 * };
 * </pre>
 * </p>
 */
@AllArgsConstructor
@Getter
public class AggregatesDto {

    /**
     * Map of locations with the number of occurrences.
     * <p>
     * Key: location name, Value: count of occurrences
     * </p>
     */
    private Map<String, Integer> locations;

    /**
     * Map of skill groups with their skills and corresponding weights/counts.
     * <p>
     * Key: skill group (e.g., "backend"), Value: map of skill names to weight/count
     * </p>
     */
    private Map<String, Map<String, Integer>> skills;
}