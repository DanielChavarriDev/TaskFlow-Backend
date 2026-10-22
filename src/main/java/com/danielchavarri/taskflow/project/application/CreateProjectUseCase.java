package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;

public interface CreateProjectUseCase {

    Project execute(String name, String description);
}