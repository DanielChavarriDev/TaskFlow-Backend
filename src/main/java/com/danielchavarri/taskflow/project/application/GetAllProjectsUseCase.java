package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import java.util.List;

public interface GetAllProjectsUseCase {

    List<Project> execute();
}