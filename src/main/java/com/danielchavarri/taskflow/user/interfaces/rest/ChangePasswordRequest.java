
package com.danielchavarri.taskflow.user.interfaces.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "La contraseña actual es obligatoria") String currentPassword,

        @NotBlank(message = "La nueva contraseña es obligatoria") @Size(min = 12, max = 72, message = "La nueva contraseña debe tener entre 12 y 72 caracteres") String newPassword

) {
}