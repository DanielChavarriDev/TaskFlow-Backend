
package com.danielchavarri.taskflow.notification.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Notification {

    private final UUID id;
    private final UUID userId;
    private final UUID taskId;
    private final NotificationType type;
    private final String title;
    private final String message;
    private final LocalDate dueDate;
    private final Instant createdAt;
    private Instant readAt;

    private Notification(
            UUID id,
            UUID userId,
            UUID taskId,
            NotificationType type,
            String title,
            String message,
            LocalDate dueDate,
            Instant createdAt,
            Instant readAt) {

        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.taskId = Objects.requireNonNull(taskId);
        this.type = Objects.requireNonNull(type);
        this.title = Objects.requireNonNull(title);
        this.message = Objects.requireNonNull(message);
        this.dueDate = Objects.requireNonNull(dueDate);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.readAt = readAt;
    }

    public static Notification create(
            UUID userId,
            UUID taskId,
            NotificationType type,
            String title,
            String message,
            LocalDate dueDate) {

        return new Notification(
                UUID.randomUUID(),
                userId,
                taskId,
                type,
                title,
                message,
                dueDate,
                Instant.now(),
                null);
    }

    public static Notification reconstitute(
            UUID id,
            UUID userId,
            UUID taskId,
            NotificationType type,
            String title,
            String message,
            LocalDate dueDate,
            Instant createdAt,
            Instant readAt) {

        return new Notification(
                id,
                userId,
                taskId,
                type,
                title,
                message,
                dueDate,
                createdAt,
                readAt);
    }

    public void markAsRead() {
        if (this.readAt == null) {
            this.readAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public boolean isRead() {
        return readAt != null;
    }
}