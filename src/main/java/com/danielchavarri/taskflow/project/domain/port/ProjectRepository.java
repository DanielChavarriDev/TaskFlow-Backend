
package com.danielchavarri.taskflow.project.domain.port;

import com.danielchavarri.taskflow.project.domain.model.Project;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Project> findAllByOwnerId(UUID ownerId);

    void deleteByIdAndOwnerId(UUID id, UUID ownerId);
}