
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GetProjectByIdService implements GetProjectByIdUseCase {

    private final ProjectRepository projectRepository;

    public GetProjectByIdService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Project execute(UUID id, UUID ownerId) {
        return projectRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }
}