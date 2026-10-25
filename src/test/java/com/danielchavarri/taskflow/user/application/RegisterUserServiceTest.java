
package com.danielchavarri.taskflow.user.application;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RegisterUserService registerUserService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        registerUserService = new RegisterUserService(
                userRepository,
                passwordEncoder);
    }

    @Test
    void shouldRegisterUserWithEncodedPassword() {
        when(userRepository.existsByEmail("daniel@example.com"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User result = registerUserService.execute(
                "Daniel",
                "Daniel@Example.com",
                "SecurePassword123!");

        assertNotNull(result.getId());
        assertEquals("Daniel", result.getName());
        assertEquals("daniel@example.com", result.getEmail());
        assertNotEquals("SecurePassword123!", result.getPasswordHash());
        assertTrue(passwordEncoder.matches(
                "SecurePassword123!",
                result.getPasswordHash()));

        verify(userRepository).existsByEmail("daniel@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        when(userRepository.existsByEmail("daniel@example.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> registerUserService.execute(
                        "Daniel",
                        "daniel@example.com",
                        "SecurePassword123!"));

        verify(userRepository).existsByEmail("daniel@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
}