package com.voidsamuraj.HireLens.dto.aggregation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a job category and the number of job listings in that category.
 *
 * Fields:
 * - `name`: the name of the category (e.g., "Engineering", "Marketing").
 * - `count`: the count of job listings in this category.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCategoryDto {

    /** The name of the job category (e.g., "Engineering", "Marketing"). */
    private String name;

    /** The number of job listings in this category. */
    private int count;
}
