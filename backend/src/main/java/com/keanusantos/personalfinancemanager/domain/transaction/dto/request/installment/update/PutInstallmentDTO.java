package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update;

import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PutInstallmentDTO(
        Long id,
       @NotNull(message = "The installment number must not be null")
       @Min(value = 1, message = "The installment number must be grater than or equal to 1") Integer installmentNumber,
       @Min(value = 0, message = "The amount must be greater than or equal to 0") @NotNull(message = "The amount cannot be null") Float amount,
       @NotNull(message = "The date must not be null") LocalDate dueDate,
       @NotNull(message = "The status must not be null") InstallmentStatus status
) {
}
