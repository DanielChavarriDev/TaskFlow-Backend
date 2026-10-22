
package com.danielchavarri.taskflow.task.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskRepository
        extends JpaRepository<TaskJpaEntity, UUID> {

    List<TaskJpaEntity> findByProjectId(UUID projectId);
}