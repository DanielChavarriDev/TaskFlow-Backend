
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;

import java.util.UUID;

public interface GetProjectByIdUseCase {

    Project execute(UUID id, UUID ownerId);
}