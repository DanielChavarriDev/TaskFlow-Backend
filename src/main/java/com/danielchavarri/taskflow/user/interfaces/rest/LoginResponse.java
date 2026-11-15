package com.danielchavarri.taskflow.user.interfaces.rest;

import com.danielchavarri.taskflow.user.domain.model.UserRole;
import java.time.Instant;
import java.util.UUID;

public record LoginResponse(
                UUID id,
                String name,
                String email,
                Instant createdAt,
                UserRole role,
                String accessToken,
                String tokenType,
                long expiresIn) {
}