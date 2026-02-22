package com.keanusantos.personalfinancemanager.domain.transaction.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.CreateTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.response.TransactionResponseDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.summary.CounterPartySummaryDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.summary.FinancialAccountSummaryDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;

import java.util.ArrayList;
import java.util.Set;

public class TransactionDTOMapper {
    public static Transaction toTransaction(CreateTransactionDTO dto, Set<Category> categories, CounterParty counterParty, FinancialAccount financialAccount, User user){
        Transaction transaction =  new Transaction();
        transaction.setDoc(dto.doc());
        transaction.setIssueDate(dto.issueDate());
        transaction.setType(dto.type());
        transaction.setDescription(dto.description());
        transaction.setCategories(categories);
        transaction.setInstallments(new ArrayList<>());
        transaction.setCounterparty(counterParty);
        transaction.setFinancialAccount(financialAccount);
        transaction.setUser(user);

        return transaction;
    }

    public static TransactionResponseDTO toResponse(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getDoc(),
                transaction.getIssueDate(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getCategories(),
                transaction.getInstallments(),
                new CounterPartySummaryDTO(transaction.getCounterparty().getId(), transaction.getCounterparty().getName()),
                new FinancialAccountSummaryDTO(transaction.getFinancialAccount().getId(), transaction.getFinancialAccount().getName()),
                transaction.getUser()
        );
    }

}
