package com.voidsamuraj.HireLens.dto.aggregation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a job listing with all relevant details.
 * <p>
 * This DTO can be used to transfer job data between services or to the UI.
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobDto {

    /** Unique identifier of the job. */
    private int id;

    /** Job title. */
    private String title;

    /** Name of the company offering the job. */
    private String companyName;

    /** URL or path to the company logo. */
    private String companyLogo;

    /** Link to the job posting. */
    private String url;

    /** Job category (e.g., "Engineering"). */
    private String category;

    /** Type of employment (e.g., Full-time, Contract). */
    private String jobType;

    /** Date when the job was published. */
    private String publicationDate;

    /** Location requirement for candidates. */
    private String candidateRequiredLocation;

    /** Salary range or information. */
    private String salary;

    /** Detailed job description. */
    private String description;
}
