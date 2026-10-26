
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

        task.changeStatus(status);

        return taskRepository.save(task);
    }
}