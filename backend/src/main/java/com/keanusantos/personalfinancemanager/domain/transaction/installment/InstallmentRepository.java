package com.keanusantos.personalfinancemanager.domain.transaction.installment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    Optional<Installment> findByIdAndTransactionUserId(Long id, Long userId);

    @Query(value = "SELECT COALESCE(SUM (i.amount),0) FROM Installment i JOIN Transaction t ON t.id = i.transaction.id JOIN Payment p ON p.id = i.payment.id WHERE i.status = PAID AND t.type = CREDIT AND t.user.id = :userId AND p.moment BETWEEN :begin AND :end")
    Double sumRevenueByActualMonthAndUserId(@Param("userId") Long userId,  @Param("begin")Instant begin, @Param("end")Instant end);

    @Query(value = "SELECT COALESCE(SUM (i.amount),0) FROM Installment i JOIN Transaction t ON t.id = i.transaction.id JOIN Payment p ON p.id = i.payment.id WHERE i.status = PAID AND t.type = DEBIT AND t.user.id = :userId AND p.moment BETWEEN :begin AND :end")
    Double sumExpenseByActualMonthAndUserId(@Param("userId") Long userId,  @Param("begin")Instant begin, @Param("end")Instant end);

    @Query(value = "SELECT COALESCE(SUM (i.amount),0) FROM installments i JOIN transactions t ON t.id = i.transaction_id JOIN payments p ON p.id = i.payment_id WHERE i.installment_status = 'PAID' AND t.transaction_type = 'CREDIT' AND t.user_id = :userId AND p.moment >= CURRENT_DATE - INTERVAL '6 months'", nativeQuery = true)
    Double sumLastSixMonthRevenueByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT COALESCE(SUM (i.amount),0) FROM installments i JOIN transactions t ON t.id = i.transaction_id JOIN payments p ON p.id = i.payment_id WHERE i.installment_status = 'PAID' AND t.transaction_type = 'DEBIT' AND t.user_id = :userId AND p.moment >= CURRENT_DATE - INTERVAL '6 months'", nativeQuery = true)
    Double sumLastSixMonthExpenseByUserId(@Param("userId") Long userId);

    @Query(value = "select i.* from transactions t join installments i on t.id = i.transaction_id join payments p on p.id = i.payment_id where t.user_id = :userId AND p.moment >= current_date - interval '6 months'", nativeQuery = true)
    List<Installment> last6MonthsInstallments(@Param("userId") Long userId);
}
