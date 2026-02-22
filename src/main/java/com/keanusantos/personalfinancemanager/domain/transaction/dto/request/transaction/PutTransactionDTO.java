package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction;


import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;


public record PutTransactionDTO (
        @NotBlank @Size(min = 3) String doc,
        @NotNull LocalDate issueDate,
        @NotNull @Enumerated(EnumType.STRING) TransactionType type,
        @NotNull @Size(min = 6, message = "Description must be at least 6 characters long") String description,
        @NotEmpty Set<Long> categories,
        @NotNull Long counterParty,
        @NotNull Long financialAccount,
        @NotNull Long user
){
}
