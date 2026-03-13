package com.keanusantos.personalfinancemanager.domain.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUser_Id(Long user);
    List<Transaction> findAllByFinancialAccountIdAndUserId(Long id, Long userId);
    Boolean existsByIdAndUser_Id(Long id, Long userId);
    Boolean existsByDocAndUser_Id(String doc, Long userId);
    Boolean existsByDocAndUser_IdAndIdNot(String doc, Long id, Long userId);
    Boolean existsByCounterpartyIdAndUser_Id(Long counterId, Long userId);
    Boolean existsByFinancialAccountIdAndUser_Id(Long financialAccountId, Long userId);
    @Query(value = "SELECT COUNT(*) > 0 FROM installments i JOIN transactions t ON t.id = i.transaction_id JOIN transaction_category tc ON tc.transaction_id = t.id  WHERE tc.category_id = :id AND i.installment_status = 'PAID'", nativeQuery = true)
    Boolean existsPaidInstallmentByCategoryId(Long id);
    @Query(value = "SELECT COUNT(*) > 0 FROM transactions t JOIN installments i ON t.id = i.transaction_id WHERE i.installment_status = 'PAID'", nativeQuery = true)
    Boolean existsPaidInstallmentById(Long id);
    Optional<Transaction> findByIdAndUser_Id(@Param("id") Long id, Long userId);
}
