package com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update;

import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PatchInstallmentDTO(
        Integer installmentNumber,
        Float amount,
        LocalDate dueDate,
        @Enumerated(EnumType.STRING) InstallmentStatus status
) {
}
