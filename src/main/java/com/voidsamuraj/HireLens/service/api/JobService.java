package com.voidsamuraj.HireLens.service.api;


import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface defining generic job service methods for fetching job listings.
 *
 * @param <T> the type of job DTO handled by the service implementation
 */
public interface JobService<T> {

    /**
     * Fetches all available jobs without filtering or pagination.
     *
     * @return list of all job DTOs of type T
     */
    @Transactional
    List<T> fetchAllJobs();

    /**
     * Fetches jobs filtered by the given query, limited to perPage results on the specified page number.
     *
     * @param query the search query to filter jobs
     * @param perPage the maximum number of jobs per page
     * @param pageNumber the page number starting from 1
     * @return list of filtered job DTOs of type T for the requested page
     */
    @Transactional
    List<T> fetchJobs(String query, int perPage, int pageNumber);

    /**
     * Default method to fetch jobs filtered by query with a page number defaulting to 1.
     *
     * @param query the search query to filter jobs
     * @param perPage the maximum number of jobs per page
     * @return list of filtered job DTOs of type T for the first page
     */
    @Transactional
    default List<T> fetchJobs(String query, int perPage){
        return fetchJobs(query, perPage, 1);
    }

    /**
     * Default method to fetch jobs filtered by query with default page number 1 and perPage 100.
     *
     * @param query the search query to filter jobs
     * @return list of filtered job DTOs of type T for the first page with 100 results
     */
    @Transactional
    default List<T> fetchJobs(String query){
        return fetchJobs(query, 100, 1);
    }

}