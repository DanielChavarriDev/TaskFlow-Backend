package com.danielchavarri.taskflow.project.application;

import java.util.UUID;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(UUID id) {
        super("No se encontró el proyecto con ID: " + id);
    }
}