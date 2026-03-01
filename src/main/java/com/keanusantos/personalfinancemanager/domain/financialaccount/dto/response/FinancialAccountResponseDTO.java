package com.keanusantos.personalfinancemanager.domain.financialaccount.dto.response;

public record FinancialAccountResponseDTO(
        Long id,
        String name,
        Float balance,
        Long userId
) {
}
