
package com.danielchavarri.taskflow.user.application;

import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class RegisterUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User execute(
            String name,
            String email,
            String password) {

        String normalizedName = name.trim();
        String normalizedEmail = email.trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException();
        }

        String passwordHash = passwordEncoder.encode(password);

        User user = User.create(
                normalizedName,
                normalizedEmail,
                passwordHash);

        return userRepository.save(user);
    }
}