
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class DeleteProjectService implements DeleteProjectUseCase {

    private final ProjectRepository projectRepository;

    public DeleteProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public void execute(UUID id) {
        projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        projectRepository.deleteById(id);
    }
}