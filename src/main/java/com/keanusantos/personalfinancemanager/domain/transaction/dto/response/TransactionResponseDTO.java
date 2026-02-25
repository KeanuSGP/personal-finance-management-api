package com.keanusantos.personalfinancemanager.domain.transaction.dto.response;

import com.keanusantos.personalfinancemanager.domain.category.dto.summary.CategorySummaryDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.summary.CounterPartySummaryDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.summary.FinancialAccountSummaryDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.user.dto.summary.UserSummaryDTO;

import java.util.List;
import java.util.Set;

public record TransactionResponseDTO(
        Long id,
        String doc,
        java.time.LocalDate issueDate,
        TransactionType type,
        String description,
        Set<CategorySummaryDTO> categories,
        List<Installment> installments,
        CounterPartySummaryDTO counterParty,
        FinancialAccountSummaryDTO financialAccount,
        UserSummaryDTO user
) {
}
