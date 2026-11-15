
package com.danielchavarri.taskflow.task.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTaskStatusAuditRepository
        extends JpaRepository<TaskStatusAuditJpaEntity, UUID> {

    List<TaskStatusAuditJpaEntity> findAllByTaskIdOrderByCreatedAtDesc(UUID taskId);
}