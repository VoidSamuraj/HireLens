package com.voidsamuraj.HireLens.dto.aggregation;

import com.voidsamuraj.HireLens.entity.JobEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents all job-related data for a single user task or query.
 *
 * This class is designed to temporarily aggregated statistics such as
 * the number of offers per location and per category, which can be used
 * to generate dashboard charts.
 *
 * <p><b>Thread-safety:</b> Uses {@link ConcurrentHashMap} internally and synchronizes
 * mutating operations to ensure consistency of aggregate counters.</p>
 *
 * <p><b>Intended use:</b> For local or single-user applications, this instance can be
 * kept in memory (e.g. in a {@code ConcurrentHashMap<UUID, UserJobData>} inside
 * a singleton service). Each task corresponds to a unique {@link UUID}.</p>
 */
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class UserJobsData {



    /** Unique identifier for this data set or task. */
    private final UUID taskId;

    /** Aggregated count of jobs by candidate location. */
    private Map<String, Integer> locationsCount = new ConcurrentHashMap<>();

    /** Aggregated map of skills and their importance with group associated to that skill. */
    private  Map<String, SkillData> skillsData = new ConcurrentHashMap<>();

    /**
     * Adds a list of new jobs to the dataset.
     * <p>
     *
     * @param newJobs list of jobs to aggregate and add or update
     */
    public synchronized void addJobs(List<JobEntity> newJobs) {
        for (JobEntity job : newJobs) {
            String loc = Optional.ofNullable(job.getCandidateRequiredLocation())
                    .filter(s -> !s.isBlank())
                    .orElse("Undefined");

            locationsCount.merge(loc, 1, Integer::sum);

            if (job.getSkills() != null) {
                job.getSkills().forEach((skill) ->
                        skillsData.merge(
                                skill.getGroup().getSkill(),
                                new SkillData(skill.getGroup().getGroupName(), skill.getLevel()),
                                (oldVal, newVal) -> new SkillData(
                                        oldVal.group(),
                                        oldVal.count() + newVal.count()
                                )
                        )
                );
            }
        }
    }

    /**
     * Returns an {@link AggregatesDto} containing aggregated locations and
     * skills
     *
     * @return aggregated DTO
     */
    public AggregatesDto getAggregates() {
        Map<String, Map<String, Integer>> grouped = skillsData.entrySet().stream()
                .peek(e -> {
                    if (e.getValue().group() == null) {
                        log.warn("Skill '{}' has null group", e.getKey());
                    }
                })
                .filter(e ->e.getValue() != null)
                .collect(Collectors.groupingBy(
                        e ->Optional.ofNullable(e.getValue().group()).orElse("UNKNOWN"),
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> Optional.of(e.getValue().count()).orElse(0)
                        )
                ));
        return new AggregatesDto(this.locationsCount, grouped);
    }

}
