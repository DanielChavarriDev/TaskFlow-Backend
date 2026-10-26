
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;

import java.util.UUID;

public interface CreateProjectUseCase {

    Project execute(UUID ownerId, String name, String description);
}