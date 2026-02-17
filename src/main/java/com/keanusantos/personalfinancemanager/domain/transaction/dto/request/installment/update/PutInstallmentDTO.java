package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment;

import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PutInstallmentDTO(
        Long id,
       @NotNull(message = "The installment number must be grater than or equal to 1")
       @Min(value = 1) Integer installmentNumber,
       @Min(value = 0, message = "The amount must be greater than or equal to 0") @NotNull(message = "Amount is required") Float amount,
       @NotNull(message = "Date is required") LocalDate dueDate,
       @NotNull(message = "Status is required") InstallmentStatus status
) {
}
