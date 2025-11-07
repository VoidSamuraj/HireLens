package com.voidsamuraj.HireLens.dto.Remotive;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.*;

import java.util.List;

/**
 * DTO representing the response from the Remotive job listings API.
 * <p>
 * Contains an optional legal notice, the total count of jobs,
 * and a list of job details.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemotiveResponseDto {

    /** Optional legal notice from the API. Maps from JSON key "0-legal-notice". */
    @Nullable
    @JsonProperty("0-legal-notice")
    private String legalNotice;

    /** Total number of jobs in the response. Maps from JSON key "job-count". */
    @JsonProperty("job-count")
    private int jobCount = 0;

    /** List of job details. Maps from JSON key "jobs". */
    @JsonProperty("jobs")
    private List<RemotiveJobDto> jobs;
}
