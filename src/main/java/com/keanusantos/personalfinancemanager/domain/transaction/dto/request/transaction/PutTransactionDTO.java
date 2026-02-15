package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction;


import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


public record PutTransactionDTO (
        @NotBlank @Size(min = 3) String doc,
        @NotNull LocalDate issueDate,
        @NotNull @Enumerated(EnumType.STRING) TransactionType type,
        @NotEmpty @Size(min = 6) String description,
        @NotNull Long counterParty,
        @NotNull Long financialAccount
){
}
