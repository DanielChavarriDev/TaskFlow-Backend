
package com.danielchavarri.taskflow.user.application;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;

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
                passwordEncoder = mock(PasswordEncoder.class);

                registerUserService = new RegisterUserService(
                                userRepository,
                                passwordEncoder);
        }

        @Test
        void shouldRegisterUserWithEncodedPassword() {
                when(userRepository.existsByEmail("daniel@example.com"))
                                .thenReturn(false);

                when(passwordEncoder.encode("SecurePassword123!"))
                                .thenReturn("$2a$12$encodedPasswordHash");

                when(userRepository.save(any(User.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                User result = registerUserService.execute(
                                "Daniel",
                                "Daniel@Example.com",
                                "SecurePassword123!");

                assertNotNull(result.getId());
                assertEquals("Daniel", result.getName());
                assertEquals("daniel@example.com", result.getEmail());
                assertEquals(
                                "$2a$12$encodedPasswordHash",
                                result.getPasswordHash());

                verify(userRepository).existsByEmail("daniel@example.com");
                verify(passwordEncoder).encode("SecurePassword123!");
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
                verify(passwordEncoder, never()).encode(anyString());
                verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldRejectPasswordExceeding72Bytes() {
                String password = "A".repeat(73);

                when(userRepository.existsByEmail("daniel@example.com"))
                                .thenReturn(false);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> registerUserService.execute(
                                                "Daniel",
                                                "daniel@example.com",
                                                password));

                verify(passwordEncoder, never()).encode(anyString());
                verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldRejectMultibytePasswordExceeding72Bytes() {
                String password = "á".repeat(37);

                assertTrue(
                                password.getBytes(StandardCharsets.UTF_8).length > 72);

                when(userRepository.existsByEmail("daniel@example.com"))
                                .thenReturn(false);

                assertThrows(
                                IllegalArgumentException.class,
                                () -> registerUserService.execute(
                                                "Daniel",
                                                "daniel@example.com",
                                                password));

                verify(passwordEncoder, never()).encode(anyString());
                verify(userRepository, never()).save(any(User.class));
        }
}