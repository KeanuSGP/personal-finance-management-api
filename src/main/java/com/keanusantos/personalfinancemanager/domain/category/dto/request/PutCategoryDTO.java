package com.keanusantos.personalfinancemanager.domain.category.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PutCategoryDTO(
        @NotEmpty(message = "The name must not be empty") @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters long") String name,
        @NotEmpty(message = "The color must not be empty") @Size(min = 6, max = 6, message = "Color must have 6 characters long") String color,
        @NotNull(message = "The user must not be null") Long user
) {
}
