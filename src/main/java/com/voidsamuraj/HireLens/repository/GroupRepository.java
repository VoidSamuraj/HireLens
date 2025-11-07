package com.voidsamuraj.HireLens.repository;

import com.voidsamuraj.HireLens.entity.Groups;
import com.voidsamuraj.HireLens.entity.JobEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Repository interface for managing {@link Groups} persistence and query operations.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD, pagination, and sorting capabilities.
 * </p>
 *
 * <p>This repository also provides custom native SQL queries to leverage PostgreSQL full-text search
 * on English and Polish job descriptions using {@code tsvector} and {@code plainto_tsquery}.</p>
 *
 * <p>Custom methods:</p>
 * <ul>
 *   <li>{@link #findExistingGroups(List<String>)} – Search for existing groups associated with skills in list</li>
 * </ul>
 */

@Repository
public interface GroupRepository extends JpaRepository<Groups, Long> {


    /**
     * Finds all Group entries that exist in the database for the given list of skills.
     *
     * @param skills list of skill names
     * @return list of matching Group entities
     */
    @Query(value = "SELECT * FROM groups WHERE skill IN (:skills)", nativeQuery = true)
    List<Groups> findExistingGroups(@Param("skills") List<String> skills);
}
