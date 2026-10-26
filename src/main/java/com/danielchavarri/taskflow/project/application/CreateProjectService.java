
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateProjectService implements CreateProjectUseCase {

    private final ProjectRepository projectRepository;

    public CreateProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public Project execute(UUID ownerId, String name, String description) {
        Project project = Project.create(ownerId, name, description);
        return projectRepository.save(project);
    }
}