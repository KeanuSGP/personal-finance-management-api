package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction;

import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.create.CreateInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateTransactionDTO(
        @Size(min = 3)
        @NotEmpty(message = "The document cannot be empty")
        String doc,

        @NotNull(message = "The date cannot be null")
        LocalDate issueDate,

        @NotNull(message = "Define the type for transaction")
        @Enumerated(EnumType.STRING)
        TransactionType type,

        String description,

        @Valid
        List<CreateInstallmentDTO> installments,

        @NotNull
        Long counterParty,
        @NotNull
        Long financialAccount
) {
}
