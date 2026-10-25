
package com.danielchavarri.taskflow.user.domain.model;

import java.time.Instant;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final Instant createdAt;

    private User(
            UUID id,
            String name,
            String email,
            String passwordHash,
            Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
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
                Instant.now());
    }

    public static User reconstitute(
            UUID id,
            String name,
            String email,
            String passwordHash,
            Instant createdAt) {
        return new User(id, name, email, passwordHash, createdAt);
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

    public Instant getCreatedAt() {
        return createdAt;
    }
}