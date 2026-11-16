
package com.danielchavarri.taskflow.user.interfaces.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(

        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede superar los 100 caracteres") String name,

        @NotBlank(message = "El correo electrónico es obligatorio") @Email(message = "El correo electrónico no es válido") @Size(max = 254, message = "El correo no puede superar los 254 caracteres") String email

) {
}