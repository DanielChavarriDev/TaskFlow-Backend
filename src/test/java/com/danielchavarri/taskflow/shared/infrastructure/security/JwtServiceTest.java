package com.danielchavarri.taskflow.shared.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

        private SecretKey secretKey;
        private JwtService jwtService;

        private final long expirationMs = 3600000L;

        @BeforeEach
        void setUp() {
                secretKey = Keys.hmacShaKeyFor(
                                "0123456789abcdef0123456789abcdef"
                                                .getBytes(StandardCharsets.UTF_8));

                jwtService = new JwtService(secretKey, expirationMs);
        }

        @Test
        void shouldGenerateValidToken() {
                UUID userId = UUID.randomUUID();
                String email = "daniel@example.com";

                String token = jwtService.generateToken(userId, email);

                assertNotNull(token);
                assertFalse(token.isBlank());

                Claims claims = jwtService.validateToken(token);

                assertEquals(userId.toString(), claims.getSubject());
                assertEquals(email, claims.get("email", String.class));
        }

        @Test
        void shouldGenerateTokenWithCorrectExpiration() {
                UUID userId = UUID.randomUUID();

                Instant beforeGeneration = Instant.now();

                String token = jwtService.generateToken(
                                userId,
                                "daniel@example.com");

                Instant afterGeneration = Instant.now();

                Claims claims = jwtService.validateToken(token);

                Instant issuedAt = claims.getIssuedAt().toInstant();
                Instant expiration = claims.getExpiration().toInstant();

                // JWT NumericDate tiene precisión de segundos.
                assertFalse(
                                issuedAt.isBefore(beforeGeneration.minusSeconds(1)));

                assertFalse(
                                issuedAt.isAfter(afterGeneration));

                assertEquals(
                                expirationMs,
                                expiration.toEpochMilli() - issuedAt.toEpochMilli());
        }

        @Test
        void shouldRejectExpiredToken() {
                Instant now = Instant.now();

                String expiredToken = Jwts.builder()
                                .subject(UUID.randomUUID().toString())
                                .claim("email", "daniel@example.com")
                                .issuedAt(Date.from(now.minusSeconds(120)))
                                .expiration(Date.from(now.minusSeconds(60)))
                                .signWith(secretKey)
                                .compact();

                assertThrows(
                                JwtException.class,
                                () -> jwtService.validateToken(expiredToken));
        }

        @Test
        void shouldRejectTokenSignedWithDifferentSecretKey() {
                SecretKey differentKey = Keys.hmacShaKeyFor(
                                "abcdef0123456789abcdef0123456789"
                                                .getBytes(StandardCharsets.UTF_8));

                String token = Jwts.builder()
                                .subject(UUID.randomUUID().toString())
                                .claim("email", "daniel@example.com")
                                .issuedAt(new Date())
                                .expiration(Date.from(
                                                Instant.now().plusSeconds(3600)))
                                .signWith(differentKey)
                                .compact();

                assertThrows(
                                JwtException.class,
                                () -> jwtService.validateToken(token));
        }

        @Test
        void shouldReturnConfiguredExpirationTime() {
                assertEquals(expirationMs, jwtService.getExpirationMs());
        }
}