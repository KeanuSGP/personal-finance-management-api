package com.keanusantos.personalfinancemanager.domain.transaction;

import com.keanusantos.personalfinancemanager.domain.transaction.dto.response.TransactionResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFinancialAccountId(Long accountId);
    Boolean existsByDocAndIdNot(String doc, Long id);
    Boolean existsByDoc(String doc);
}
