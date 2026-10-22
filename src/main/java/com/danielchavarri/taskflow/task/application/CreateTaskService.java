
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;

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

    public Task execute(
            UUID projectId,
            String title,
            String description,
            TaskPriority priority,
            LocalDate dueDate) {

        projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el proyecto con ID: " + projectId));

        Task task = Task.create(
                projectId,
                title,
                description,
                priority,
                dueDate);

        return taskRepository.save(task);
    }
}