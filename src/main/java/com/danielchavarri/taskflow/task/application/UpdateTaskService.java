
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class UpdateTaskService {

    private final TaskRepository taskRepository;

    public UpdateTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Task execute(
            UUID id,
            UUID ownerId,
            String title,
            String description,
            TaskPriority priority,
            LocalDate dueDate) {

        Task task = taskRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.updateDetails(title, description, priority, dueDate);

        return taskRepository.save(task);
    }
}