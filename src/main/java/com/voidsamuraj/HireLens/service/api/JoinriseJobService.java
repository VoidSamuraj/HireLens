package com.voidsamuraj.HireLens.service.api;

import com.voidsamuraj.HireLens.dto.Joinrise.JoinriseJobDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for fetching Joinrise job listings via REST API.
 *
 * Uses {@link RestTemplate} to perform HTTP GET requests to Joinrise's public job API.
 * Provides methods to fetch jobs with optional keyword filtering and simple pagination
 * as well as to fetch all available jobs.
 *
 * The fetchJobs method fetches jobs by page and applies a token-based filter on title, company,
 * category, and job type properties to match the query.
 *
 * Both methods return empty lists if no jobs are found or on API errors.
 *
 * Operations are marked as transactional for Spring transaction management compatibility,
 * although these methods primarily perform read-only external API calls.
 */
@Service
@AllArgsConstructor
@Slf4j
public class JoinriseJobService implements JobService<JoinriseJobDto.RiseJobDto>{

    private final RestTemplate restTemplate;

    /**
     * Fetches Joinrise job listings filtered by query, with pagination.
     * Applies token-based keyword filtering across multiple job fields.
     *
     * @param query the search query string; if null or blank, no filtering is applied after fetching
     * @param perPage the number of jobs to fetch per page
     * @param pageNumber the page number in the Joinrise API pagination
     * @return list of filtered JoinriseJobDto.RiseJobDto matching the query; empty list if none found
     */
    @Override
    @Transactional

    public List<JoinriseJobDto.RiseJobDto> fetchJobs(String query, int perPage, int pageNumber) {
        try {
            return Optional.ofNullable(restTemplate.getForObject("https://api.joinrise.io/api/v1/jobs/public?limit=" + perPage + "&sortedBy=createdAt&sort=des&page=" + pageNumber, JoinriseJobDto.class))
                    .map(r -> r.getResult().getJobs())
                    .orElse(List.of())
                    .stream()
                    .filter(it -> matches(it, query))
                    .toList();
        }catch (RestClientException e){
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Fetches all available Joinrise public job listings without filtering or pagination.
     *
     * @return list of all Joinrise jobs or empty list on error or no data
     */
    @Override
    @Transactional
    public List<JoinriseJobDto.RiseJobDto> fetchAllJobs() {
        try{
            return Optional.ofNullable(restTemplate.getForObject("https://api.joinrise.io/api/v1/jobs/public", JoinriseJobDto.class))
                    .map(r -> r.getResult().getJobs())
                    .orElse(List.of());
        }catch (RestClientException e){
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Checks if a job matches the query by testing if all tokens of the query
     * appear in at least one of the job's title, company name, category, or job type.
     *
     * @param job the job to test
     * @param query the search query string
     * @return true if job matches all query tokens, false otherwise
     */
    private boolean matches(JoinriseJobDto.RiseJobDto job, String query) {
        if (query == null || query.isBlank()) return true;
        String title = job.getTitle() != null ? job.getTitle().toLowerCase() : "";
        String company = (job.getOwner() != null && job.getOwner().getCompanyName() != null) ? job.getOwner().getCompanyName().toLowerCase() : "";
        String category = job.getCategory() != null ? job.getCategory().toLowerCase() : "";
        String jobType = job.getType() != null ? job.getType().toLowerCase() : "";

        String[] tokens = query.toLowerCase().split("\\s+");

        return Arrays.stream(tokens)
                .allMatch(token -> title.contains(token)
                        || company.contains(token)
                        || category.contains(token)
                        || jobType.contains(token));
    }
}
