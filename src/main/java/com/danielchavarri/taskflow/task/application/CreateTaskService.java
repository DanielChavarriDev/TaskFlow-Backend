
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CreateTaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public CreateTaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Task execute(
            UUID projectId,
            UUID ownerId,
            String title,
            String description,
            TaskPriority priority,
            LocalDate dueDate) {

        Project project = projectRepository
                .findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el proyecto con ID: " + projectId));

        Task task = Task.create(
                project.getId(),
                title,
                description,
                priority,
                dueDate);

        return taskRepository.save(task);
    }
}