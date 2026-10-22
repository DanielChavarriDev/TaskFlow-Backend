package com.danielchavarri.taskflow.project.interfaces.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "Máximo 100 caracteres") String name,

        @Size(max = 500, message = "Máximo 500 caracteres") String description) {
}