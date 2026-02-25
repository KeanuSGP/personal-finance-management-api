package com.keanusantos.personalfinancemanager.domain.payment.dto.response;

import java.time.Instant;

public record ResponsePaymentDTO(
        Long id,
        Instant moment,
        Long financialAccount,
        Long installment
) {
}
