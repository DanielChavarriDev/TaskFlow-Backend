
package com.danielchavarri.taskflow.user.interfaces.rest;

import com.danielchavarri.taskflow.user.application.ChangePasswordService;
import com.danielchavarri.taskflow.user.application.UpdateUserProfileService;
import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/me")
public class UserProfileController {

    private final UserRepository userRepository;
    private final UpdateUserProfileService updateUserProfileService;
    private final ChangePasswordService changePasswordService;

    public UserProfileController(
            UserRepository userRepository,
            UpdateUserProfileService updateUserProfileService,
            ChangePasswordService changePasswordService) {

        this.userRepository = userRepository;
        this.updateUserProfileService = updateUserProfileService;
        this.changePasswordService = changePasswordService;
    }

    @GetMapping
    public UserResponse getProfile(Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return UserResponse.from(user);
    }

    @PutMapping
    public UserResponse updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateUserProfileRequest request) {

        UUID userId = (UUID) authentication.getPrincipal();

        User updatedUser = updateUserProfileService.execute(
                userId,
                request.name(),
                request.email());

        return UserResponse.from(updatedUser);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        UUID userId = (UUID) authentication.getPrincipal();

        changePasswordService.execute(
                userId,
                request.currentPassword(),
                request.newPassword());
    }
}