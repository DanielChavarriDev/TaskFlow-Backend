
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;

import java.util.List;
import java.util.UUID;

public interface GetAllProjectsUseCase {

    List<Project> execute(UUID ownerId);
}