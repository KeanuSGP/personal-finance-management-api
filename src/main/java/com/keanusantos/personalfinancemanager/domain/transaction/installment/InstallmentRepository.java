package com.keanusantos.personalfinancemanager.domain.transaction.installment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    Optional<Installment> findByIdAndTransactionUserId(Long id, Long userId);

    @Query(value = "SELECT COALESCE(SUM (i.amount),0) FROM Installment i JOIN Transaction t ON t.id = i.transaction.id JOIN Payment p ON p.id = i.payment.id WHERE i.status = PAID AND t.type = CREDIT AND t.user.id = :userId AND p.moment BETWEEN :begin AND :end")
    Double sumRevenueByActualMonthAndUserId(@Param("userId") Long userId,  @Param("begin")Instant begin, @Param("end")Instant end);

    @Query(value = "SELECT COALESCE(SUM (i.amount),0) FROM Installment i JOIN Transaction t ON t.id = i.transaction.id JOIN Payment p ON p.id = i.payment.id WHERE i.status = PAID AND t.type = DEBIT AND t.user.id = :userId AND p.moment BETWEEN :begin AND :end")
    Double sumExpenseByActualMonthAndUserId(@Param("userId") Long userId,  @Param("begin")Instant begin, @Param("end")Instant end);
}
