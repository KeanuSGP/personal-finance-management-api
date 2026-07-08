package com.keanusantos.personalfinancemanager.domain.dashboard;

import com.keanusantos.personalfinancemanager.domain.payment.dto.response.ResponsePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.response.TransactionResponseDTO;

import java.util.List;

public record DashboardDTO(
        Float totalBalance,
        List<ResponsePaymentDTO> latestPayments,
        List<TransactionResponseDTO> latestTransactions,
        Double monthlyRevenue,
        Double monthlyExpense
) {
}
