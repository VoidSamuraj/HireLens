package com.voidsamuraj.HireLens.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a skill associated with a job.
 * <p>
 * Maps to the "job_skills" table and stores individual skills, their group/category,
 * and proficiency level for a specific job.
 * </p>
 *
 * <p>Fields:</p>
 * <ul>
 *   <li>{@code id} – auto-generated database identifier</li>
 *   <li>{@code job} – reference to the {@link JobEntity} this skill belongs to</li>
 *   <li>{@code group} – skill category or group name (e.g., "Programming", "Databases")</li>
 *   <li>{@code skill} – name of the individual skill (e.g., "Java", "PostgreSQL")</li>
 *   <li>{@code level} – proficiency level or weight of the skill (integer value)</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_skills")
public class SkillEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private JobEntity job;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "skill", referencedColumnName = "skill", nullable = true)
    private Groups group;

    private Integer level;
}
