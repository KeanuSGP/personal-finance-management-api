package com.keanusantos.personalfinancemanager.domain.payment.dto.response;

import com.keanusantos.personalfinancemanager.domain.payment.dto.summary.FinancialAccountPaymentSummaryDTO;

import java.time.ZonedDateTime;

public record ResponsePaymentDTO(
        Long id,
        String moment,
        FinancialAccountPaymentSummaryDTO financialAccount,
        Long installment
) {
}
