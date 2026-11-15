package com.danielchavarri.taskflow.user.infrastructure.persistence;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.model.UserRole;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Configuration
public class AdminAccountInitializer {

    @Bean
    @ConditionalOnProperty(name = "taskflow.admin.provision-enabled", havingValue = "true")
    ApplicationRunner provisionAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${taskflow.admin.email}") String email,
            @Value("${taskflow.admin.password}") String password,
            @Value("${taskflow.admin.name:TaskFlow Administrator}") String name) {

        return args -> {
            String normalizedEmail = email.trim()
                    .toLowerCase(Locale.ROOT);

            if (normalizedEmail.isBlank() || password.isBlank()) {
                throw new IllegalStateException(
                        "La configuración del administrador está incompleta.");
            }

            var existing = userRepository.findByEmail(normalizedEmail);

            if (existing.isPresent()) {
                if (existing.get().getRole() != UserRole.ADMIN) {
                    throw new IllegalStateException(
                            "El correo configurado para ADMIN ya pertenece " +
                                    "a una cuenta USER. Revisa la cuenta manualmente.");
                }

                // Ya existe un administrador. No cambiar su contraseña ni su rol.
                return;
            }

            User admin = User.reconstitute(
                    UUID.randomUUID(),
                    name.trim(),
                    normalizedEmail,
                    passwordEncoder.encode(password),
                    UserRole.ADMIN,
                    Instant.now());

            userRepository.save(admin);
        };
    }
}