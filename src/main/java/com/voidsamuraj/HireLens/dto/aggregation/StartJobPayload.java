package com.voidsamuraj.HireLens.dto.aggregation;

import com.voidsamuraj.HireLens.entity.JobLevel;
import lombok.Data;

/**
 * Payload DTO for starting a job search with criteria.
 * <p>
 * Contains parameters used to initiate a job search, including query string,
 * desired job level, whether to include jobs with unknown levels, and
 * the maximum number of job offers to fetch.
 * </p>
 */
@Data
public class StartJobPayload {

    /** Search query string, e.g., "Java Developer" or "Frontend Engineer". */
    private String query;

    /** Desired job experience level (e.g., JUNIOR, MID, SENIOR). */
    private JobLevel level;

    /** Whether to include jobs with unknown or unspecified levels. */
    private boolean includeUnknown;

    /** Maximum number of job offers to fetch. */
    private int maxJobOffers;
}