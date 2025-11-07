package com.voidsamuraj.HireLens.service.api;

import com.voidsamuraj.HireLens.dto.Adzuna.AdzunaJobDto;
import com.voidsamuraj.HireLens.dto.Adzuna.AdzunaResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for fetching job listings from Adzuna API.
 *
 * Uses Spring's {@link RestTemplate} to perform HTTP GET requests to Adzuna's job search API.
 * Methods support fetching jobs filtered by query with pagination, and fetching a default batch of jobs.
 *
 * API credentials (app ID and key) are injected from application properties.
 *
 * Requests include HTTP headers specifying JSON response acceptance.
 *
 * Returned results are parsed into {@link AdzunaResponseDto} and mapped to a list of {@link AdzunaJobDto}.
 *
 * The methods are transactional to ensure Spring transaction management integration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdzunaJobService implements JobService<AdzunaJobDto> {

    @Value("${adzuna.api.key}")
    private String apiKey;
    @Value("${adzuna.api.id}")
    private String appId;

    private final RestTemplate restTemplate;

    /**
     * Fetches job listings from Adzuna API matching the search query on the specified page with given size.
     *
     * @param query the search query to filter jobs
     * @param perPage number of job listings per page
     * @param pageNumber the page number starting from 1
     * @return list of AdzunaJobDto matching the query and page parameters; empty list if none found
     */
    @Override
    @Transactional
    public List<AdzunaJobDto> fetchJobs(String query, int perPage, int pageNumber) {
        try{
            String url = "http://api.adzuna.com/v1/api/jobs/gb/search/" + pageNumber
                    + "?app_id=" + appId
                    + "&app_key=" + apiKey
                    + "&results_per_page=" + perPage
                    + "&what=" + query;

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<AdzunaResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AdzunaResponseDto.class);

            return Optional.ofNullable(response.getBody())
                    .map(AdzunaResponseDto::getResults)
                    .orElse(List.of());
        }catch (RestClientException e){
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Fetches a default batch of 100 jobs from Adzuna API without query filtering.
     *
     * @return list of up to 100 recent AdzunaJobDto listings; empty list if none found
     */
    @Override
    @Transactional
    public List<AdzunaJobDto> fetchAllJobs() {
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<AdzunaResponseDto> response = restTemplate.exchange(
                    "http://api.adzuna.com/v1/api/jobs/gb/search/1?app_id=" + appId + "&app_key=" + apiKey + "&results_per_page=100&content-type=application/json",
                    HttpMethod.GET,
                    entity,
                    AdzunaResponseDto.class);
            return Optional.ofNullable(response.getBody())
                    .map(AdzunaResponseDto::getResults)
                    .orElse(List.of());
        }catch (RestClientException e){
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

}
