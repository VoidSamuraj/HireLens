package com.voidsamuraj.HireLens.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Entity representing a job listing stored in the database.
 * <p>
 * Maps to the "job_entity" table and contains detailed information about the job,
 * its source API, experience level, publication details, candidate location, salary,
 * description, and associated skills.
 * </p>
 *
 * <p>Fields:</p>
 * <ul>
 *   <li>{@code id} – auto-generated database identifier</li>
 *   <li>{@code apiName} – source API of the job listing ({@link ApiName})</li>
 *   <li>{@code apiId} – identifier of the job in the source API</li>
 *   <li>{@code title}, {@code companyName}, {@code companyLogo}, {@code url}, {@code category}, {@code jobType} – job and company details</li>
 *   <li>{@code experienceLevel} – job experience level ({@link JobLevel})</li>
 *   <li>{@code publicationDate} – date when the job was published</li>
 *   <li>{@code candidateRequiredLocation} – required candidate location</li>
 *   <li>{@code salary} – salary range or information</li>
 *   <li>{@code description} – full text description of the job</li>
 *   <li>{@code skills} – list of {@link SkillEntity} representing grouped skills and levels</li>
 *   <li>{@code tsvEn} – full-text search vector (for PostgreSQL tsvector, read-only)</li>
 * </ul>
 *
 * <p>Methods:</p>
 * <ul>
 *   <li>{@link #setSkills(Map)} – populates the {@code skills} list from a nested map of group -> skill -> level</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>
 * JobEntity job = JobEntity.builder()
 *     .apiName(ApiName.REMOTIVE)
 *     .apiId("12345")
 *     .title("Senior Java Developer")
 *     .companyName("TechCorp")
 *     .experienceLevel(JobLevel.SENIOR)
 *     .build();
 *
 * Map&lt;String, Map&lt;String, Integer&gt;&gt; skillsMap = Map.of(
 *     "Programming", Map.of("Java", 5, "Spring", 4),
 *     "Databases", Map.of("PostgreSQL", 3)
 * );
 *
 * job.setSkills(skillsMap);
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_entity")
public class JobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "api_name", nullable = false)
    private ApiName apiName;

    @Column(name = "api_id", nullable = false, length = 100)
    private String apiId;

    private String title;
    private String companyName;
    private String companyLogo;
    private String url;
    private String category;
    private String jobType;
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level", nullable = false)
    private JobLevel experienceLevel;
    private String publicationDate;
    private String candidateRequiredLocation;
    private String salary;

    @Column(columnDefinition = "TEXT")
    private String description;
/*
    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @MapKeyColumn(name = "technology")
    @Column(name = "level")
    private Map<String, Integer> skills = new HashMap<>();
    */
    @Builder.Default
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillEntity> skills = new ArrayList<>();

    /**
     * Populates the {@code skills} list from a nested map of group -> skill -> level.
     * Existing skills are cleared before adding new ones.
     *
     * @param skillMap nested map containing skill groups and their corresponding skills with levels
     */
   /* public void setSkillsGrouped(Map<String, Map<String, Integer>> skillMap) {
        this.skills.clear();
        skillMap.forEach((group, skillsInGroup) -> {
            skillsInGroup.forEach((skillInGroup, level) ->{
                SkillEntity js = new SkillEntity();
                js.setJob(this);
                js.setGroup(group);
                js.setSkill(skillInGroup);
                js.setLevel(level);
                this.skills.add(js);

            });
        });
    }*/
    /**
     * Populates the {@code skills} list from a map of skill -> level.
     * Existing skills are cleared before adding new ones.
     *
     * @param skillMap map containing skill and their level
     */
    public void setSkills(Map<String, Integer> skillMap) {
        this.skills.clear();
        skillMap.forEach((skillInGroup, level) ->{
                SkillEntity js = new SkillEntity();
                js.setJob(this);
                js.setGroup(new Groups(skillInGroup,null));
                js.setLevel(level);
                this.skills.add(js);
            });
    }


    @Column(name = "tsv_en", insertable = false, updatable = false)
    private String tsvEn;
}