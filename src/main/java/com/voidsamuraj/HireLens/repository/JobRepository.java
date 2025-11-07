package com.voidsamuraj.HireLens.repository;

import com.voidsamuraj.HireLens.entity.JobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Repository interface for managing {@link JobEntity} persistence and query operations.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD, pagination, and sorting capabilities.
 * </p>
 *
 * <p>This repository also provides custom native SQL queries to leverage PostgreSQL full-text search
 * on English and Polish job descriptions using {@code tsvector} and {@code plainto_tsquery}.</p>
 *
 * <p>Custom methods:</p>
 * <ul>
 *   <li>{@link #searchJobs(String, Pageable)} – Executes a full-text search on English text fields
 *       and orders results by relevance using {@code ts_rank}. Supports pagination.</li>
 *   <li>{@link #getSkillSums(String)} – Retrieves aggregated sums of skill levels for jobs matching
 *       the full-text search query. Useful for skill-based analytics.</li>
 *   <li>{@link #getLocationCounts(String)} – Counts job listings grouped by candidate-required location
 *       for a given search query. Useful for location-based dashboards.</li>
 *   <li>{@link #findKeysIn(Collection)} – Checks which job keys (apiName:apiId) exist in the database.</li>
 * </ul>
 */

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {

    /** DTO-like record representing a skill and its aggregated total level. */
    record SkillCount(String skill, Long total) {}

    /**
     * Performs a full-text search on {@code tsv_en} (English text vector) of job descriptions.
     * <p>
     * Results are ordered by relevance (ts_rank) and support pagination via {@link Pageable}.
     * </p>
     *
     * @param query    search query string
     * @param pageable pagination information
     * @return page of {@link JobEntity} matching the search
     */
    @Query(
            value = """
            SELECT * 
            FROM job_entity 
            WHERE tsv_en @@ plainto_tsquery('english', :query)
            ORDER BY ts_rank(tsv_en, plainto_tsquery('english', :query)) DESC
            """,
            countQuery = """
            SELECT count(*) 
            FROM job_entity 
            WHERE tsv_en @@ plainto_tsquery('english', :query)
            """,
            nativeQuery = true
    )
    @Transactional
    Page<JobEntity> searchJobs(@Param("query") String query, Pageable pageable);

    /**
     * Aggregates skill levels for jobs matching a full-text search query.
     * <p>
     * Returns a list of {@link SkillCount} sorted by total level descending.
     * </p>
     *
     * @param query search query string
     * @return list of skills with aggregated levels
     */
    @Query(
            value = """
        SELECT 
            js.skill AS skill,
            gs.group_name AS group_name,
            SUM(js.level) AS total_level
        FROM job_skills js
        JOIN job_entity je ON js.job_id = je.id
        JOIN groups gs ON js.skill = gs.skill
        WHERE je.tsv_en @@ plainto_tsquery('english', :query)
        GROUP BY js.skill, gs.group_name
        ORDER BY total_level DESC;
        """,
            nativeQuery = true
    )
    @Transactional
    List<SkillProjection> getSkillSums(@Param("query") String query);

    /**
     * Counts job listings grouped by candidate-required location for a given search query.
     *
     * @param query search query string
     * @return list of {@link SkillCount} where 'skill' field represents the location and 'total' is the count
     */
    @Query(
            value = """
    SELECT je.candidate_required_location, COUNT(*) AS total_count
    FROM job_entity je
    WHERE je.tsv_en @@ plainto_tsquery('english', :query)
    GROUP BY je.candidate_required_location
    ORDER BY total_count DESC
    """,
            nativeQuery = true
    )
    @Transactional
    List<SkillCount> getLocationCounts(@Param("query") String query);

    /**
     * Returns a set of existing job keys in the format {@code apiName:apiId} for the provided collection.
     *
     * @param keys collection of job keys to check
     * @return set of keys that exist in the database
     */
    @Query("SELECT CONCAT(j.apiName, ':', j.apiId) " +
            "FROM JobEntity j " +
            "WHERE CONCAT(j.apiName, ':', j.apiId) IN :keys")
    Set<String> findKeysIn(@Param("keys") Collection<String> keys);

    interface SkillProjection {
        String getSkill();
        String getGroupName();
        int getTotalLevel();
    }
}