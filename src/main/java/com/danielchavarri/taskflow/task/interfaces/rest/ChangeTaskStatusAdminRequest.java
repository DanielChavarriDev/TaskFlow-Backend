
package com.danielchavarri.taskflow.task.interfaces.rest;

import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeTaskStatusAdminRequest(
        @NotNull(message = "El nuevo estado es obligatorio.") TaskStatus newStatus,

        @NotBlank(message = "El motivo de la intervención es obligatorio.") @Size(max = 500, message = "El motivo no puede superar los 500 caracteres.") String reason) {
}