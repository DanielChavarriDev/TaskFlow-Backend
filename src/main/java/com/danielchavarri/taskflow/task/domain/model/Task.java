
package com.danielchavarri.taskflow.task.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Task {

    private final UUID id;
    private final UUID projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private final Instant createdAt;

    private Task(
            UUID id,
            UUID projectId,
            String title,
            String description,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueDate,
            Instant createdAt) {

        this.id = Objects.requireNonNull(id);
        this.projectId = Objects.requireNonNull(projectId);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.title = validateTitle(title);
        this.description = normalizeDescription(description);
        this.status = Objects.requireNonNull(status);
        this.priority = Objects.requireNonNull(priority);
        this.dueDate = dueDate;
    }

    public static Task create(
            UUID projectId,
            String title,
            String description,
            TaskPriority priority,
            LocalDate dueDate) {

        return new Task(
                UUID.randomUUID(),
                projectId,
                title,
                description,
                TaskStatus.PENDING,
                priority,
                dueDate,
                Instant.now());
    }

    public static Task reconstitute(
            UUID id,
            UUID projectId,
            String title,
            String description,
            TaskStatus status,
            TaskPriority priority,
            LocalDate dueDate,
            Instant createdAt) {

        return new Task(
                id,
                projectId,
                title,
                description,
                status,
                priority,
                dueDate,
                createdAt);
    }

    public void updateDetails(
            String title,
            String description,
            TaskPriority priority,
            LocalDate dueDate) {

        this.title = validateTitle(title);
        this.description = normalizeDescription(description);
        this.priority = Objects.requireNonNull(priority);
        this.dueDate = dueDate;
    }

    public void changeStatus(TaskStatus status) {
        this.status = Objects.requireNonNull(status);
    }

    private static String validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "El título de la tarea es obligatorio.");
        }

        String normalized = title.trim();

        if (normalized.length() > 150) {
            throw new IllegalArgumentException(
                    "El título no puede superar los 150 caracteres.");
        }

        return normalized;
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        String normalized = description.trim();

        if (normalized.length() > 1000) {
            throw new IllegalArgumentException(
                    "La descripción no puede superar los 1000 caracteres.");
        }

        return normalized;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}