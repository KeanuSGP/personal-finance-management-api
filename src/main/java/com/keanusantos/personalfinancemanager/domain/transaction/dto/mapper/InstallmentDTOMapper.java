package com.keanusantos.personalfinancemanager.domain.transaction.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PutInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PatchInstallmentDTO;


public class InstallmentDTOMapper {

    public static Installment updateDTOtoInstallment(PatchInstallmentDTO installmentDTO, Transaction transaction) {
        return new Installment(
                null,
                installmentDTO.installmentNumber(),
                installmentDTO.amount(),
                installmentDTO.dueDate(),
                installmentDTO.status(),
                transaction
        );
    }

    public static Installment putDTOtoInstallment(PutInstallmentDTO installmentDTO, Transaction transaction) {
        return new Installment(
                installmentDTO.id(),
                installmentDTO.installmentNumber(),
                installmentDTO.amount(),
                installmentDTO.dueDate(),
                installmentDTO.status(),
                transaction
        );
    }

    public static PatchInstallmentDTO installmentToPatchDTO(Installment installment) {
        return new PatchInstallmentDTO(
                installment.getInstallmentNumber(),
                installment.getAmount(),
                installment.getDueDate(),
                installment.getStatus()
                );
    }

    public static PutInstallmentDTO toPutInstallmentDTO(Installment installment) {
        return new PutInstallmentDTO(
                installment.getId(),
                installment.getInstallmentNumber(),
                installment.getAmount(),
                installment.getDueDate(),
                installment.getStatus()
        );
    }
}
