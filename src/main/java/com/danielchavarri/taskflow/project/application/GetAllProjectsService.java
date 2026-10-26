
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetAllProjectsService implements GetAllProjectsUseCase {

    private final ProjectRepository projectRepository;

    public GetAllProjectsService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> execute(UUID ownerId) {
        return projectRepository.findAllByOwnerId(ownerId);
    }
}