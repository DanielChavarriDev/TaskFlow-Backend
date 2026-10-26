
package com.danielchavarri.taskflow.task.infrastructure.persistence;

import com.danielchavarri.taskflow.task.domain.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskPersistenceMapper {

    public TaskJpaEntity toEntity(Task task) {
        return new TaskJpaEntity(
                task.getId(),
                task.getProjectId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt());
    }

    public Task toDomain(TaskJpaEntity entity) {
        return Task.reconstitute(
                entity.getId(),
                entity.getProjectId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getPriority(),
                entity.getDueDate(),
                entity.getCreatedAt());
    }
}