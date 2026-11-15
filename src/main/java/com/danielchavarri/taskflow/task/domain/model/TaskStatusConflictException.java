
package com.danielchavarri.taskflow.task.domain.model;

public class TaskStatusConflictException extends RuntimeException {

    public TaskStatusConflictException(String message) {
        super(message);
    }
}