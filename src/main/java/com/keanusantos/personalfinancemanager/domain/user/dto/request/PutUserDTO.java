package com.keanusantos.personalfinancemanager.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PutUserDTO(
        @NotEmpty(message = "The name must not empty") @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters long") String name,
        @NotEmpty(message = "The email must not empty") @Email(message = "Invalid email") String email,
        @NotEmpty(message = "The password must not me empty") @Size(min = 4, max = 6, message = "password must be between 4 and 6 characters long") String password
) {
}
