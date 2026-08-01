package com.keanusantos.personalfinancemanager.domain.transaction.dto.summary;

import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;

import java.time.LocalDate;

public record TransactionSummaryDTO(
        Long id,
        String doc,
        LocalDate issueDate,
        TransactionType type,
        String description
) {
}
