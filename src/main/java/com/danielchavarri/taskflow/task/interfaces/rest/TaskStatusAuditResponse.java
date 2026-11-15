
package com.danielchavarri.taskflow.task.interfaces.rest;

import com.danielchavarri.taskflow.task.domain.model.TaskStatusAudit;

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

    public static TaskStatusAuditResponse from(TaskStatusAudit audit) {

        return new TaskStatusAuditResponse(
                audit.id(),
                audit.taskId(),
                audit.adminUserId(),
                audit.previousStatus().name(),
                audit.newStatus().name(),
                audit.reason(),
                audit.createdAt());
    }
}