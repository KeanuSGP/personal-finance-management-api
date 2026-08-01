package com.keanusantos.personalfinancemanager.domain.payment.dto.response;

public record ResponsePaymentDTO(
        Long id,
        String moment,
        Long transactionId,
        String transactionDoc,
        Long financialAccountId,
        String finAcc,
        Long installmentId,

        Integer installmentNumber,
        Float installmentAmount
) {
}
