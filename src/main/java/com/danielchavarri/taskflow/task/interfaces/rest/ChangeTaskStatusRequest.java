
package com.danielchavarri.taskflow.task.interfaces.rest;

import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeTaskStatusRequest(

        @NotNull(message = "El estado es obligatorio.") TaskStatus status) {
}