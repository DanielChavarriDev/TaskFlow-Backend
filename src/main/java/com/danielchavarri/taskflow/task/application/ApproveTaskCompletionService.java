package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.model.TaskStatusAudit;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import com.danielchavarri.taskflow.task.domain.port.TaskStatusAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ApproveTaskCompletionService {

    private final TaskRepository taskRepository;
    private final TaskStatusAuditRepository auditRepository;

    public ApproveTaskCompletionService(
            TaskRepository taskRepository,
            TaskStatusAuditRepository auditRepository) {

        this.taskRepository = taskRepository;
        this.auditRepository = auditRepository;
    }

    @Transactional
    public Task execute(
            UUID taskId,
            UUID adminUserId,
            String reason) {

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "El motivo de la aprobación es obligatorio.");
        }

        String normalizedReason = reason.trim();

        if (normalizedReason.length() > 500) {
            throw new IllegalArgumentException(
                    "El motivo no puede superar los 500 caracteres.");
        }

        Task task = taskRepository.findByIdForUpdate(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        TaskStatus previousStatus = task.getStatus();

        task.approveCompletion();

        Task savedTask = taskRepository.save(task);

        TaskStatusAudit audit = new TaskStatusAudit(
                UUID.randomUUID(),
                taskId,
                adminUserId,
                previousStatus,
                savedTask.getStatus(),
                normalizedReason,
                Instant.now());

        auditRepository.save(audit);

        return savedTask;
    }

}