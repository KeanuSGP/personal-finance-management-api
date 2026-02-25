package com.keanusantos.personalfinancemanager.domain.payment.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreatePaymentDTO(
        @NotNull(message = "The account must not be null") Long financialAccount
) {
}
