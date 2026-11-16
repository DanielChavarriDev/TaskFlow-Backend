
package com.danielchavarri.taskflow.user.interfaces.rest;

import com.danielchavarri.taskflow.user.application.GetAllUsersService;
import com.danielchavarri.taskflow.user.application.UpdateUserStatusService;
import com.danielchavarri.taskflow.user.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final GetAllUsersService getAllUsersService;
    private final UpdateUserStatusService updateUserStatusService;

    public AdminUserController(
            GetAllUsersService getAllUsersService,
            UpdateUserStatusService updateUserStatusService) {

        this.getAllUsersService = getAllUsersService;
        this.updateUserStatusService = updateUserStatusService;
    }

    @GetMapping
    public List<AdminUserResponse> getAllUsers() {
        return getAllUsersService.execute()
                .stream()
                .map(AdminUserResponse::from)
                .toList();
    }

    @PatchMapping("/{userId}/status")
    @ResponseStatus(HttpStatus.OK)
    public AdminUserResponse updateStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserStatusRequest request,
            Authentication authentication) {

        UUID adminId = (UUID) authentication.getPrincipal();

        User updatedUser = updateUserStatusService.execute(
                userId,
                adminId,
                request.active());

        return AdminUserResponse.from(updatedUser);
    }
}