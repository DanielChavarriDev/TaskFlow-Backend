
package com.danielchavarri.taskflow.user.application;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class ChangePasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(
            UUID userId,
            String currentPassword,
            String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "La contraseña actual es incorrecta");
        }

        if (passwordEncoder.matches(
                newPassword,
                user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "La nueva contraseña debe ser diferente de la actual");
        }

        if (newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                    "La contraseña no puede superar los 72 bytes en UTF-8");
        }

        String newPasswordHash = passwordEncoder.encode(newPassword);

        User updatedUser = User.reconstitute(
                user.getId(),
                user.getName(),
                user.getEmail(),
                newPasswordHash,
                user.getRole(),
                user.getCreatedAt());

        userRepository.save(updatedUser);
    }
}