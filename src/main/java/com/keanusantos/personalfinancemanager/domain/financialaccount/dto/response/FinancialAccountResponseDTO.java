package com.keanusantos.personalfinancemanager.domain.financialaccount.dto.response;

import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;

public record FinancialAccountResponseDTO(
        Long id,
        String name,
        Float balance,
        Long userId
) {
}
