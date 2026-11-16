
package com.danielchavarri.taskflow.notification.interfaces.rest;

import com.danielchavarri.taskflow.notification.domain.model.Notification;
import com.danielchavarri.taskflow.notification.domain.model.NotificationType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID taskId,
        NotificationType type,
        String title,
        String message,
        LocalDate dueDate,
        Instant createdAt,
        Instant readAt,
        boolean read) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTaskId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getDueDate(),
                notification.getCreatedAt(),
                notification.getReadAt(),
                notification.isRead());
    }
}