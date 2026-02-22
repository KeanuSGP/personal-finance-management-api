package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction;


import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PutInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;


public record PutTransactionDTO (
        @Size(min = 3, max = 20, message = "Doc must be between 3 and 20 characters long")
        @NotEmpty(message = "The document must not be null")
        String doc,

        @NotNull(message = "The issueDate must not be null")
        LocalDate issueDate,

        @NotNull(message = "The type must not be null")
        @Enumerated(EnumType.STRING)
        TransactionType type,

        @NotEmpty(message = "The description must not be null")
        @Size(min = 3, message = "Description must have at least 3 characters long")
        String description,
        @NotEmpty(message = "The categories must not be empty") Set<Long> categories,
        @NotNull(message = "The counterparty must not be null") Long counterParty,
        @NotNull(message = "The financial account must not be empty") Long financialAccount,
        @NotNull(message = "The user must not be empty") Long user
){
}
