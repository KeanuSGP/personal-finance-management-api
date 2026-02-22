package com.keanusantos.personalfinancemanager.domain.counterparty.dto.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CounterPartyResponseDTO(
        @NotNull Long id,
        @NotEmpty String name,
        @NotEmpty String taxId,
        @NotNull Long user
) {
}
