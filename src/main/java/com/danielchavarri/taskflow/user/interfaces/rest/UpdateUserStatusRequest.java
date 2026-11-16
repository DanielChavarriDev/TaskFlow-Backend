
package com.danielchavarri.taskflow.user.interfaces.rest;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull(message = "El estado de la cuenta es obligatorio") Boolean active) {
}