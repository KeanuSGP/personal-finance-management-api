package com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PutAccountDTO(
        @NotEmpty(message = "The name must not be empty") @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters long") String name,
        @NotNull(message = "The balance must not be null") @Min(value = 0, message = "The balance must be zero ou greater") Float balance,
        @NotNull(message = "The user must not be null") Long user
) {
}
