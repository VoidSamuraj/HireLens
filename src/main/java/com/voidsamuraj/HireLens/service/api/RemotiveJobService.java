package com.voidsamuraj.HireLens.service.api;

import com.voidsamuraj.HireLens.dto.Remotive.RemotiveJobDto;
import com.voidsamuraj.HireLens.dto.Remotive.RemotiveResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for fetching Remotive job listings via REST API.
 *
 * Uses {@link RestTemplate} to perform HTTP GET requests to Remotive's public API.
 * Provides methods to fetch jobs matching a query with pagination parameters,
 * as well as to fetch a default set of jobs without query filters.
 *
 * Methods are marked transactional for participation in Spring-managed transactions,
 * though primarily involve read-only REST API operations.
 */
@Service
@AllArgsConstructor
@Slf4j
public class RemotiveJobService implements JobService<RemotiveJobDto>{

    private final RestTemplate restTemplate;

    /**
     * Fetches job listings from Remotive API matching the given query.
     *
     * @param query the search query string for filtering job listings
     * @param perPage the number of jobs to fetch per request
     * @param pageNumber currently unused (Remotive API does not support page numbers in this call)
     * @return list of RemotiveJobDto objects matching the query; empty list if none found or on error
     */
    @Override
    @Transactional
    public List<RemotiveJobDto> fetchJobs(String query, int perPage, int pageNumber) {
        try{
            return Optional.ofNullable(restTemplate.getForObject("https://remotive.com/api/remote-jobs?search="+query+"&limit="+perPage, RemotiveResponseDto.class))
                    .map(RemotiveResponseDto::getJobs)
                    .orElse(List.of());
        }catch (RestClientException e){
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Fetches a default list of recent job listings from Remotive API,
     * limited to 100 jobs.
     *
     * @return list of RemotiveJobDto objects; empty list if none found or on error
     */
    @Override
    @Transactional
    public List<RemotiveJobDto> fetchAllJobs() {
        try{
            return Optional.ofNullable(restTemplate.getForObject("https://remotive.com/api/remote-jobs?limit=100", RemotiveResponseDto.class))
                    .map(RemotiveResponseDto::getJobs)
                    .orElse(List.of());
        }catch (RestClientException e){
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }
}
