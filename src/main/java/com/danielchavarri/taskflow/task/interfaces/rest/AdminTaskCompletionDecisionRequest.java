package com.danielchavarri.taskflow.task.interfaces.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminTaskCompletionDecisionRequest(

        @NotBlank(message = "El motivo de la decisión es obligatorio.") @Size(max = 500, message = "El motivo no puede superar los 500 caracteres.") String reason

) {
}