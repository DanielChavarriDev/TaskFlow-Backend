
package com.danielchavarri.taskflow.task.interfaces.rest;

import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTaskRequest(

        @NotNull(message = "El proyecto es obligatorio.") UUID projectId,

        @NotBlank(message = "El título es obligatorio.") @Size(max = 150, message = "El título no puede superar los 150 caracteres.") String title,

        @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres.") String description,

        @NotNull(message = "La prioridad es obligatoria.") TaskPriority priority,

        LocalDate dueDate) {
}