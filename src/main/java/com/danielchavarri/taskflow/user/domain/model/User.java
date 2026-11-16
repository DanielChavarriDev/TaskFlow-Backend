
package com.danielchavarri.taskflow.user.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final Instant createdAt;
    private final boolean active;

    private User(
            UUID id,
            String name,
            String email,
            String passwordHash,
            UserRole role,
            Instant createdAt,
            boolean active) {

        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.role = Objects.requireNonNull(role);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.active = active;
    }

    public static User create(
            String name,
            String email,
            String passwordHash) {

        return new User(
                UUID.randomUUID(),
                name,
                email,
                passwordHash,
                UserRole.USER,
                Instant.now(),
                true);
    }

    // Mantiene compatibilidad con las llamadas anteriores.
    public static User reconstitute(
            UUID id,
            String name,
            String email,
            String passwordHash,
            UserRole role,
            Instant createdAt) {

        return reconstitute(
                id, name, email, passwordHash, role, createdAt, true);
    }

    public static User reconstitute(
            UUID id,
            String name,
            String email,
            String passwordHash,
            UserRole role,
            Instant createdAt,
            boolean active) {

        return new User(
                id,
                name,
                email,
                passwordHash,
                role,
                createdAt,
                active);
    }

    public User changeActiveStatus(boolean active) {
        return reconstitute(
                id,
                name,
                email,
                passwordHash,
                role,
                createdAt,
                active);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }
}