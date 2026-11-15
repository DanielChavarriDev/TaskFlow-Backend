
package com.danielchavarri.taskflow.task.application;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.model.TaskStatusAudit;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import com.danielchavarri.taskflow.task.domain.port.TaskStatusAuditRepository;

@Service
public class AdminInterveneTaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusAuditRepository auditRepository;

    public AdminInterveneTaskService(
            TaskRepository taskRepository,
            TaskStatusAuditRepository auditRepository) {

        this.taskRepository = taskRepository;
        this.auditRepository = auditRepository;
    }

    @Transactional
    public Task execute(
            UUID taskId,
            UUID adminUserId,
            TaskStatus newStatus,
            String reason) {

        Objects.requireNonNull(taskId, "El ID de la tarea es obligatorio.");
        Objects.requireNonNull(
                adminUserId,
                "El ID del administrador es obligatorio.");
        Objects.requireNonNull(
                newStatus,
                "El nuevo estado es obligatorio.");

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo de la intervención es obligatorio.");
        }

        String normalizedReason = reason.trim();

        if (normalizedReason.length() > 500) {
            throw new IllegalArgumentException(
                    "El motivo no puede superar los 500 caracteres.");
        }

        Task task = taskRepository.findByIdForUpdate(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        TaskStatus previousStatus = task.getStatus();

        task.applyAdministrativeIntervention(newStatus);

        Task savedTask = taskRepository.save(task);

        TaskStatusAudit audit = new TaskStatusAudit(
                UUID.randomUUID(),
                taskId,
                adminUserId,
                previousStatus,
                newStatus,
                normalizedReason,
                Instant.now());

        auditRepository.save(audit);

        return savedTask;
    }
}