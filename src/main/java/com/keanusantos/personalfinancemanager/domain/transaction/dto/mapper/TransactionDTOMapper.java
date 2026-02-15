package com.keanusantos.personalfinancemanager.domain.transaction.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.CreateTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.response.TransactionResponseDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.summary.CounterPartySummaryDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.summary.FinancialAccountSummaryDTO;

import java.util.ArrayList;

public class TransactionDTOMapper {
    public static Transaction toTransaction(CreateTransactionDTO dto, CounterParty counterParty, FinancialAccount financialAccount){
        Transaction transaction =  new Transaction();
        transaction.setDoc(dto.doc());
        transaction.setIssueDate(dto.issueDate());
        transaction.setType(dto.type());
        transaction.setDescription(dto.description());
        transaction.setInstallments(new ArrayList<>());
        transaction.setCounterparty(counterParty);
        transaction.setFinancialAccount(financialAccount);

        return transaction;
    }

    public static TransactionResponseDTO toResponse(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getDoc(),
                transaction.getIssueDate(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getInstallments(),
                new CounterPartySummaryDTO(transaction.getCounterparty().getId(), transaction.getCounterparty().getName()),
                new FinancialAccountSummaryDTO(transaction.getFinancialAccount().getId(), transaction.getFinancialAccount().getName())
        );
    }

}
