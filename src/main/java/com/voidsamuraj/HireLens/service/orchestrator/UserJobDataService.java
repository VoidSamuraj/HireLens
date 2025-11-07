package com.voidsamuraj.HireLens.service.orchestrator;

import com.voidsamuraj.HireLens.dto.aggregation.AggregatesDto;
import com.voidsamuraj.HireLens.dto.aggregation.SkillData;
import com.voidsamuraj.HireLens.entity.JobEntity;
import com.voidsamuraj.HireLens.dto.aggregation.UserJobsData;
import com.voidsamuraj.HireLens.service.ai.AiClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Service responsible for managing the lifecycle and content of the current {@link UserJobsData} instance.
 * <p>
 * This service is designed for single-user or single-task applications.
 * It acts as an in-memory container that stores job-related data aggregated during
 * data download or processing (e.g., job offers, category counts, location stats).
 * </p>
 *
 * <p>
 * Typical usage:
 * <ul>
 *   <li>Initialize a new data session using {@link #init(UUID jobId)} before starting a new download task.</li>
 *   <li>Add new jobs incrementally using {@link #addJobs(List)} during batch processing.</li>
 *   <li>Access current aggregated results using .</li>
 * </ul>
 * </p>
 *
 * <p><b>Thread-safety:</b> The underlying {@link UserJobsData} structure uses concurrent maps,
 * so it is safe for use from multiple threads in most single-user scenarios.</p>
 */
@Service
@RequiredArgsConstructor
public class UserJobDataService {
    private final AiClientService aiClientService;

    /**
     * The current active job data instance for this application session.
     * Only one data set is kept in memory at a time.
     */
    private UserJobsData currentData;

    /**
     * Initializes a new {@link UserJobsData} instance, clearing old one and assigns it as the current active dataset.
     * <p>
     * This method should be called before any data download or aggregation begins.
     * </p>
     * @param jobId job id associated with dataset
     */
    public void init(UUID jobId) {
        this.currentData = new UserJobsData(jobId);
    }

    /**
     * Returns the map of locations and their occurrence counts.
     *
     * @return Map of locations and their occurrence counts.
     */
    public Map<String,Integer> getLocationsCount() {
        return (currentData != null) ? currentData.getLocationsCount() : null;
    }

    /**
     * Returns the map of categories/skills their occurrence counts and associated group.
     *
     * @return Map of categories/skills their occurrence counts and associated group.
     */
    public Map<String, SkillData> getSkillsData() {
        return (currentData != null) ? currentData.getSkillsData() : null;
    }

    public AggregatesDto getAggregates(){
        return (currentData != null) ? currentData.getAggregates() : null;
        //return currentData.getAggregates();
    }
    /**
     * Adds a list of new job entities to the current data session.
     * <p>
     * This method updates related aggregates
     * (e.g. counts by location and category).
     * </p>
     *
     * @param newJobs list of new {@link JobEntity} objects to include in the data set
     */
    public void addJobs(List<JobEntity> newJobs) {
        if (currentData != null) {
            currentData.addJobs(newJobs);
        }
    }

    /**
     * Replaces the current aggregated location counts with the provided map.
     * <p>
     * This method updates the {@code currentData} only if it is not {@code null}.
     *
     * @param locationsCount a map where keys are location names and values are their respective counts
     */
    public void setLocations(Map<String, Integer> locationsCount){
        if (currentData != null) {
            if (locationsCount != null) {
                Map<String, Integer> sanitized = locationsCount.entrySet().stream()
                        .filter(e -> e.getKey() != null)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));

                currentData.setLocationsCount(sanitized);
            } else {
                currentData.setLocationsCount(Map.of());
            }
        }
    }

    /**
     * Replaces the current aggregated skill counts with the provided map.
     * <p>
     * This method updates the {@code currentData} only if it is not {@code null}.
     *
     * @param skillDataMap a map where keys are skill names and values is {@link SkillData} their respective counts
     */
    public void setSkills(Map<String, SkillData> skillDataMap){
        if (currentData != null) {
            currentData.setSkillsData(skillDataMap);
        }
    }

}