package com.keanusantos.personalfinancemanager.domain.transaction.dto.response;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.transaction.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.summary.CounterPartySummaryDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.summary.FinancialAccountSummaryDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.user.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;
import java.util.Set;

public record TransactionResponseDTO(
        Long id,
        String doc,
        java.time.LocalDate issueDate,
        TransactionType type,
        String description,
        Set<Category> categories,
        List<Installment> installments,
        CounterPartySummaryDTO counterParty,
        FinancialAccountSummaryDTO financialAccount,
        User user
) {
}
