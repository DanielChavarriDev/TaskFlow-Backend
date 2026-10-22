package com.danielchavarri.taskflow.project.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataProjectRepository
        extends JpaRepository<ProjectJpaEntity, UUID> {
}