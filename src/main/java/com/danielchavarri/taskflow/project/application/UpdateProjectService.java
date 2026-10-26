
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateProjectService implements UpdateProjectUseCase {

    private final ProjectRepository projectRepository;

    public UpdateProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public Project execute(
            UUID id,
            UUID ownerId,
            String name,
            String description) {

        Project project = projectRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        project.updateDetails(name, description);

        return projectRepository.save(project);
    }
}