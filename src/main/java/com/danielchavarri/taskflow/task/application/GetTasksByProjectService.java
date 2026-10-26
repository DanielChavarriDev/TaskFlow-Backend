
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetTasksByProjectService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public GetTasksByProjectService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> execute(UUID projectId, UUID ownerId) {
        projectRepository.findByIdAndOwnerId(projectId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el proyecto con ID: " + projectId));

        return taskRepository.findByProjectIdAndOwnerId(projectId, ownerId);
    }
}