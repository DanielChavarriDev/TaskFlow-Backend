
package com.danielchavarri.taskflow.task.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskRepository
        extends JpaRepository<TaskJpaEntity, UUID> {

    List<TaskJpaEntity> findByProjectId(UUID projectId);

    List<TaskJpaEntity> findByStatusAndPriority(
            TaskStatus status,
            TaskPriority priority);

    List<TaskJpaEntity> findByStatus(TaskStatus status);

    List<TaskJpaEntity> findByPriority(TaskPriority priority);
}