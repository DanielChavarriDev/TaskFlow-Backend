
package com.danielchavarri.taskflow.project.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProjectRepository
                extends JpaRepository<ProjectJpaEntity, UUID> {

        Optional<ProjectJpaEntity> findByIdAndOwnerId(
                        UUID id,
                        UUID ownerId);

        List<ProjectJpaEntity> findAllByOwnerId(UUID ownerId);

        void deleteByIdAndOwnerId(UUID id, UUID ownerId);
}