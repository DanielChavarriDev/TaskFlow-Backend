
package com.danielchavarri.taskflow.user.infrastructure.persistence;

import com.danielchavarri.taskflow.user.domain.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataUserRepository
        extends JpaRepository<UserJpaEntity, UUID> {

    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findById(UUID id);

    long countByRoleAndActiveTrue(UserRole role);
}