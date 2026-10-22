package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;

import java.util.UUID;

public interface UpdateProjectUseCase {

    Project execute(UUID id, String name, String description);
}