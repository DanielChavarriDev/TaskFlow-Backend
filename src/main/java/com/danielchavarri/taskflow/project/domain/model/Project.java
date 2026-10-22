package com.danielchavarri.taskflow.project.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Project {

    private final UUID id;
    private String name;
    private String description;
    private final Instant createdAt;

    private Project(
            UUID id,
            String name,
            String description,
            Instant createdAt) {
        this.id = Objects.requireNonNull(id);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.name = validateName(name);
        this.description = normalizeDescription(description);
    }

    public static Project create(String name, String description) {
        return new Project(
                UUID.randomUUID(),
                name,
                description,
                Instant.now());
    }

    public static Project reconstitute(
            UUID id,
            String name,
            String description,
            Instant createdAt) {
        return new Project(id, name, description, createdAt);
    }

    public void updateDetails(String name, String description) {
        this.name = validateName(name);
        this.description = normalizeDescription(description);
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del proyecto es obligatorio.");
        }

        String normalized = name.trim();

        if (normalized.length() > 100) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar los 100 caracteres.");
        }

        return normalized;
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        String normalized = description.trim();

        if (normalized.length() > 500) {
            throw new IllegalArgumentException(
                    "La descripción no puede superar los 500 caracteres.");
        }

        return normalized;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}