
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteProjectService implements DeleteProjectUseCase {

    private final ProjectRepository projectRepository;

    public DeleteProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public void execute(UUID id, UUID ownerId) {
        projectRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        projectRepository.deleteByIdAndOwnerId(id, ownerId);
    }
}