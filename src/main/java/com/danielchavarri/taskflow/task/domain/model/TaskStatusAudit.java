
package com.danielchavarri.taskflow.task.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TaskStatusAudit(
        UUID id,
        UUID taskId,
        UUID adminUserId,
        TaskStatus previousStatus,
        TaskStatus newStatus,
        String reason,
        Instant createdAt) {
}