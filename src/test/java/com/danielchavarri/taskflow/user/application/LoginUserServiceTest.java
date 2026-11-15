
package com.danielchavarri.taskflow.user.application;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.model.UserRole;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginUserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private LoginUserService loginUserService;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        loginUserService = new LoginUserService(
                userRepository,
                passwordEncoder);

        user = User.reconstitute(
                UUID.randomUUID(),
                "Daniel",
                "daniel@example.com",
                "$2a$12$encodedPasswordHash",
                UserRole.USER,
                Instant.now());
    }

    @Test
    void shouldLoginSuccessfullyWithValidCredentials() {
        when(userRepository.findByEmail("daniel@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "SecurePassword123!",
                user.getPasswordHash()))
                .thenReturn(true);

        User result = loginUserService.execute(
                "daniel@example.com",
                "SecurePassword123!");

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).findByEmail("daniel@example.com");
        verify(passwordEncoder).matches(
                "SecurePassword123!",
                user.getPasswordHash());
    }

    @Test
    void shouldNormalizeEmailDuringLogin() {
        when(userRepository.findByEmail("daniel@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "SecurePassword123!",
                user.getPasswordHash()))
                .thenReturn(true);

        User result = loginUserService.execute(
                " Daniel@Example.com ",
                "SecurePassword123!");

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());

        verify(userRepository).findByEmail("daniel@example.com");
    }

    @Test
    void shouldRejectLoginWhenEmailDoesNotExist() {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUserService.execute(
                        "unknown@example.com",
                        "SecurePassword123!"));

        verify(passwordEncoder, never()).matches(
                anyString(),
                anyString());
    }

    @Test
    void shouldRejectLoginWithIncorrectPassword() {
        when(userRepository.findByEmail("daniel@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword123!",
                user.getPasswordHash()))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginUserService.execute(
                        "daniel@example.com",
                        "WrongPassword123!"));

        verify(passwordEncoder).matches(
                "WrongPassword123!",
                user.getPasswordHash());
    }
}