package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction;

import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatchTransactionDTO(
        @Size(min = 3) String doc,
        LocalDate issueDate,
        @Enumerated(EnumType.STRING) TransactionType type,
        String description,
        Long counterParty,
        Long financialAccount
){
}
