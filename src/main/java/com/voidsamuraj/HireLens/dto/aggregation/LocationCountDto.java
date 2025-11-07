package com.voidsamuraj.HireLens.dto.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a location and the number of its occurrences.
 * <p>
 * This DTO can be used to aggregate or summarize job locations, events, or other
 * items by location.
 * </p>
 * <p>
 * Example usage:
 * <pre>
 * LocationCountDto location1 = new LocationCountDto("New York", 5);
 * LocationCountDto location2 = new LocationCountDto("London", 3);
 * </pre>
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationCountDto {

    /** Name of the location. **/
    private String location;

    /** Number of occurrences at this location. **/
    private int count;
}