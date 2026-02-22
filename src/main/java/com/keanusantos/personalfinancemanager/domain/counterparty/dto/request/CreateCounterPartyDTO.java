package com.keanusantos.personalfinancemanager.domain.counterparty.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCounterPartyDTO(
        @NotEmpty(message = "The name must not be empty") @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters long") String name,
        @NotEmpty(message = "TaxId field must not be empty") @Size(min = 11, max = 14, message = "TaxId must be between 11 and 14 characters long") String taxId,
        @NotNull(message = "The user must not be null") Long user
) {
}
