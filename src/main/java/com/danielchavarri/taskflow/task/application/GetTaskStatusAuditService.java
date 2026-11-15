
package com.danielchavarri.taskflow.task.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danielchavarri.taskflow.task.domain.model.TaskStatusAudit;
import com.danielchavarri.taskflow.task.domain.port.TaskStatusAuditRepository;

@Service
public class GetTaskStatusAuditService {

    private final TaskStatusAuditRepository auditRepository;

    public GetTaskStatusAuditService(
            TaskStatusAuditRepository auditRepository) {

        this.auditRepository = auditRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskStatusAudit> execute(UUID taskId) {

        Objects.requireNonNull(
                taskId,
                "El ID de la tarea es obligatorio.");

        return auditRepository.findAllByTaskId(taskId);
    }
}