
package com.danielchavarri.taskflow.task.infrastructure.persistence;

import com.danielchavarri.taskflow.task.domain.model.TaskStatusAudit;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusAuditPersistenceMapper {

    public TaskStatusAuditJpaEntity toEntity(TaskStatusAudit audit) {
        return new TaskStatusAuditJpaEntity(
                audit.id(),
                audit.taskId(),
                audit.adminUserId(),
                audit.previousStatus(),
                audit.newStatus(),
                audit.reason(),
                audit.createdAt());
    }

    public TaskStatusAudit toDomain(TaskStatusAuditJpaEntity entity) {
        return new TaskStatusAudit(
                entity.getId(),
                entity.getTaskId(),
                entity.getAdminUserId(),
                entity.getPreviousStatus(),
                entity.getNewStatus(),
                entity.getReason(),
                entity.getCreatedAt());
    }
}