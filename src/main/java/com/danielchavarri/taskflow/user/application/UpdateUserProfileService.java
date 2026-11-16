
package com.danielchavarri.taskflow.user.application;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class UpdateUserProfileService {

    private final UserRepository userRepository;

    public UpdateUserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User execute(UUID userId, String name, String email) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String normalizedName = name.trim();
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (!currentUser.getEmail().equals(normalizedEmail)
                && userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException();
        }

        User updatedUser = User.reconstitute(
                currentUser.getId(),
                normalizedName,
                normalizedEmail,
                currentUser.getPasswordHash(),
                currentUser.getRole(),
                currentUser.getCreatedAt());

        return userRepository.save(updatedUser);
    }
}