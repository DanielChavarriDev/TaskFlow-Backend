package com.danielchavarri.taskflow.task.application;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(UUID taskId) {
        super("No se encontró la tarea con ID: " + taskId);
    }
}