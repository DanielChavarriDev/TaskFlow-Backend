
package com.danielchavarri.taskflow.task.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTaskRepository
                extends JpaRepository<TaskJpaEntity, UUID> {

        @Query(value = """
                        SELECT t.*
                        FROM tasks t
                        INNER JOIN projects p ON p.id = t.project_id
                        WHERE p.user_id = :ownerId
                        """, nativeQuery = true)
        List<TaskJpaEntity> findAllByOwnerId(
                        @Param("ownerId") UUID ownerId);

        @Query(value = """
                        SELECT t.*
                        FROM tasks t
                        INNER JOIN projects p ON p.id = t.project_id
                        WHERE t.id = :taskId
                          AND p.user_id = :ownerId
                        """, nativeQuery = true)
        Optional<TaskJpaEntity> findByIdAndOwnerId(
                        @Param("taskId") UUID taskId,
                        @Param("ownerId") UUID ownerId);

        @Query(value = """
                        SELECT t.*
                        FROM tasks t
                        INNER JOIN projects p ON p.id = t.project_id
                        WHERE t.project_id = :projectId
                          AND p.user_id = :ownerId
                        """, nativeQuery = true)
        List<TaskJpaEntity> findByProjectIdAndOwnerId(
                        @Param("projectId") UUID projectId,
                        @Param("ownerId") UUID ownerId);

        @Query(value = """
                        SELECT t.*
                        FROM tasks t
                        INNER JOIN projects p ON p.id = t.project_id
                        WHERE p.user_id = :ownerId
                          AND (:status IS NULL OR t.status = :status)
                          AND (:priority IS NULL OR t.priority = :priority)
                        """, nativeQuery = true)
        List<TaskJpaEntity> findByFiltersAndOwnerId(
                        @Param("ownerId") UUID ownerId,
                        @Param("status") String status,
                        @Param("priority") String priority);

        @Modifying
        @Query(value = """
                        DELETE FROM tasks t
                        USING projects p
                        WHERE t.project_id = p.id
                          AND t.id = :taskId
                          AND p.user_id = :ownerId
                        """, nativeQuery = true)
        int deleteByIdAndOwnerId(
                        @Param("taskId") UUID taskId,
                        @Param("ownerId") UUID ownerId);
}