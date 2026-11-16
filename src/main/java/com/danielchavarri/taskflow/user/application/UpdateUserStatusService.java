
package com.danielchavarri.taskflow.user.application;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.model.UserRole;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateUserStatusService {

    private final UserRepository userRepository;

    public UpdateUserStatusService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User execute(
            UUID targetUserId,
            UUID authenticatedAdminId,
            boolean active) {

        if (targetUserId.equals(authenticatedAdminId) && !active) {
            throw new IllegalArgumentException(
                    "No puedes desactivar tu propia cuenta administrativa");
        }

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (user.isActive() == active) {
            return user;
        }

        if (!active && user.getRole() == UserRole.ADMIN) {
            long activeAdmins = userRepository.countActiveByRole(UserRole.ADMIN);

            if (activeAdmins <= 1) {
                throw new IllegalArgumentException(
                        "No se puede desactivar el último administrador activo");
            }
        }

        User updatedUser = user.changeActiveStatus(active);

        return userRepository.save(updatedUser);
    }
}