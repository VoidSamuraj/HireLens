package com.voidsamuraj.HireLens.dto.Remotive;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.*;


/**
 * DTO representing a remote job listing from Remotive.
 * <p>
 * Includes job details such as ID, title, company information, category, job type,
 * publication date, candidate location requirements, salary, and description.
 * <p>
 * This class is designed to map directly to JSON responses from the Remotive API.
 * Fields are annotated with {@link JsonProperty} when the JSON key differs from the Java field name.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemotiveJobDto {

    /** Unique identifier of the job. Defaults to -1 if not provided. */
    private Long id = -1L;

    /** URL of the job posting. */
    private String url;

    /** Job title. */
    private String title;

    /** Name of the company offering the job. Maps from JSON key "company_name". */
    @JsonProperty("company_name")
    private String companyName;

    /** URL to the company's logo. Nullable. Maps from JSON key "company_logo". */
    @Nullable
    @JsonProperty("company_logo")
    private String companyLogo;

    /** Job category (e.g., "Engineering"). Nullable. */
    @Nullable
    private String category;

    /** Type of employment (e.g., "Full-time"). Nullable. Maps from JSON key "job_type". */
    @Nullable
    @JsonProperty("job_type")
    private String jobType;

    /** Date of publication. Nullable. Maps from JSON key "publication_date". */
    @Nullable
    @JsonProperty("publication_date")
    private String publicationDate;

    /** Candidate location requirement (e.g., "Remote"). Nullable. Maps from JSON key "candidate_required_location". */
    @Nullable
    @JsonProperty("candidate_required_location")
    private String candidateRequiredLocation;

    /** Salary range or information. Nullable. */
    @Nullable
    private String salary;

    /** Detailed job description. */
    private String description;
}