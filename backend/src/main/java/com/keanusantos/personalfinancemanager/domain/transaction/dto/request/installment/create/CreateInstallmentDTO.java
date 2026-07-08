package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.create;

import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateInstallmentDTO(
        @Min(value = 0, message = "The amount must be equal 0 or greater") @NotNull(message = "The amount must not be null") Float amount,
        @NotNull(message = "Date must not be null") LocalDate dueDate
) {
}
