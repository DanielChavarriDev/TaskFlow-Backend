
package com.danielchavarri.taskflow.user.interfaces.rest;

import com.danielchavarri.taskflow.user.application.LoginUserService;
import com.danielchavarri.taskflow.user.application.RegisterUserService;
import com.danielchavarri.taskflow.user.domain.model.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.danielchavarri.taskflow.shared.infrastructure.security.JwtService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserService registerUserService;
    private final LoginUserService loginUserService;
    private final JwtService jwtService;

    public AuthController(
            RegisterUserService registerUserService,
            LoginUserService loginUserService, JwtService jwtService) {
        this.registerUserService = registerUserService;
        this.loginUserService = loginUserService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegisterUserRequest request) {

        User user = registerUserService.execute(
                request.name(),
                request.email(),
                request.password());

        return UserResponse.from(user);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        User user = loginUserService.execute(
                request.email(),
                request.password());

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail());

        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                token,
                "Bearer",
                jwtService.getExpirationMs());
    }
}