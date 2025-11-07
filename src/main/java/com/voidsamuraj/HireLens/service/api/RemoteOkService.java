package com.voidsamuraj.HireLens.service.api;

import com.voidsamuraj.HireLens.client.remoteok.api.DefaultApi;
import com.voidsamuraj.HireLens.client.remoteok.dto.Job;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service implementation for fetching RemoteOK job listings using asynchronous API client.
 *
 * Uses a reactive {@link DefaultApi} client to retrieve job listings as {@link  reactor.core.publisher.Flux} streams,
 * then filters and paginates the results according to the search query, page number, and page size.
 *
 * The fetchJobs method supports keyword filtering by checking if all search tokens
 * appear in the job's title, description, or tags.
 *
 * The fetchAllJobs method returns all available jobs from the RemoteOK API.
 *
 * Both methods block on the reactive streams to return synchronized lists.
 */
@Service
@AllArgsConstructor
@Slf4j
public class RemoteOkService implements JobService<Job>{

    private final DefaultApi apiClient;

    /**
     * Fetches job listings from RemoteOK API filtered by the query, paged by perPage and pageNumber.
     * If the query is empty or null, returns unfiltered job listings.
     *
     * @param query the search query string; if null or blank, no filtering is applied
     * @param perPage number of job listings per page
     * @param pageNumber the page number, starting from 1
     * @return list of jobs matching the query and pagination parameters
     */
    @Override
    @Transactional
    public List<Job> fetchJobs(String query, int perPage, int pageNumber) {
        try{
            if (query == null || query.isBlank()) {
                return apiClient.getJobs().skip((long) (pageNumber-1) *perPage).take(perPage).collectList().block();
            }
            return  apiClient.getJobs() // return Flux<T>
                    .filter(job -> matches(job, query))
                    .skip((long) perPage *(pageNumber-1))
                    .take(perPage)
                    .collectList()
                    .block();
        }catch (RestClientException e){
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Fetches all job listings from RemoteOK API.
     *
     * @return list of all available jobs
     */
    @Override
    @Transactional
    public List<Job> fetchAllJobs() {
        try{
            return apiClient.getJobs().collectList().block();
        }catch (RestClientException e){
            log.error(e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Checks if the job matches the search query.
     * Splits the query into tokens and verifies if all tokens are found
     * in at least one of title, description, or tags (case-insensitive).
     *
     * @param job the job to test
     * @param query the search query string
     * @return true if the job matches all query tokens, false otherwise
     */
    private boolean matches(Job job, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }

        String title = job.getSlug() != null ? job.getSlug().toLowerCase() : "";
        String description = job.getDescription() != null ? job.getDescription().toLowerCase() : "";
        List<String> tags = job.getTags() != null ? job.getTags() : List.of();
        String[] tokens = query.toLowerCase().split("\\s+");
        // String position = job.getPosition() != null ? job.getPosition() : "";
        //String legal = job.getLegal();

        //System.out.println("position: "+position+"      legal: "+legal);
        return Arrays.stream(tokens)
                .allMatch(token -> title.contains(token) || description.contains(token)||tags.contains(token));

    }
}