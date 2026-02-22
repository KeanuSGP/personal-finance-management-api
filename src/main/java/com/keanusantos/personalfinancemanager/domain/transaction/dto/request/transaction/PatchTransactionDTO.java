package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public record PatchTransactionDTO(
        @Size(min = 3, max = 20, message = "Doc must be between 3 and 20 characters long") String doc,
        LocalDate issueDate,
        @Enumerated(EnumType.STRING) TransactionType type,
        @Size(min = 3, message = "Description must have at least 3 characters long") String description,
        Set<Long> categories,
        Long counterParty,
        Long financialAccount,
        Long user
){
}
