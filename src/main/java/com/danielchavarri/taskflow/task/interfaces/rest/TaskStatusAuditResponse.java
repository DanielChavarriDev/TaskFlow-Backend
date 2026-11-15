
package com.danielchavarri.taskflow.task.interfaces.rest;

import com.danielchavarri.taskflow.task.infrastructure.persistence.TaskStatusAuditJpaEntity;

import java.time.Instant;
import java.util.UUID;

public record TaskStatusAuditResponse(
        UUID id,
        UUID taskId,
        UUID adminUserId,
        String previousStatus,
        String newStatus,
        String reason,
        Instant createdAt) {
    public static TaskStatusAuditResponse from(
            TaskStatusAuditJpaEntity audit) {

        return new TaskStatusAuditResponse(
                audit.getId(),
                audit.getTaskId(),
                audit.getAdminUserId(),
                audit.getPreviousStatus().name(),
                audit.getNewStatus().name(),
                audit.getReason(),
                audit.getCreatedAt());
    }
}