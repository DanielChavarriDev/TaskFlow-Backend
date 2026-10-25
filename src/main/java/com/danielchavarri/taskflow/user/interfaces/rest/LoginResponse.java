
package com.danielchavarri.taskflow.user.interfaces.rest;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(
        UUID id,
        String name,
        String email,
        Instant createdAt,
        String accessToken,
        String tokenType,
        long expiresIn) {
}