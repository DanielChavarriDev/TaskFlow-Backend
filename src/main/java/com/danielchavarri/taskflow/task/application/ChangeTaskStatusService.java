
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangeTaskStatusService {

    private final TaskRepository taskRepository;

    public ChangeTaskStatusService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task execute(UUID id, UUID ownerId, TaskStatus status) {
        Task task = taskRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new TaskNotFoundException(id));

        switch (status) {
            case PENDING ->
                throw new IllegalArgumentException(
                        "No se puede cambiar una tarea a pendiente mediante esta operación.");

            case IN_PROGRESS -> {
                if (task.getStatus() == TaskStatus.PENDING) {
                    task.start();
                } else if (task.getStatus() == TaskStatus.IN_REVIEW) {
                    task.rejectCompletion();
                } else {
                    throw new IllegalStateException(
                            "La tarea no puede volver a en ejecución desde su estado actual.");
                }
            }

            case IN_REVIEW -> task.requestCompletion();

            case COMPLETED ->
                throw new IllegalArgumentException(
                        "La finalización debe ser aprobada por el propietario del proyecto.");
        }

        return taskRepository.save(task);
    }
}