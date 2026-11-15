
package com.danielchavarri.taskflow.task.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danielchavarri.taskflow.task.infrastructure.persistence.SpringDataTaskStatusAuditRepository;
import com.danielchavarri.taskflow.task.infrastructure.persistence.TaskStatusAuditJpaEntity;

@Service
public class GetTaskStatusAuditService {

    private final SpringDataTaskStatusAuditRepository auditRepository;

    public GetTaskStatusAuditService(
            SpringDataTaskStatusAuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskStatusAuditJpaEntity> execute(UUID taskId) {
        Objects.requireNonNull(taskId, "El ID de la tarea es obligatorio.");

        return auditRepository
                .findAllByTaskIdOrderByCreatedAtDesc(taskId);
    }
}