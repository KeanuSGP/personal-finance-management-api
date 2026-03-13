package com.keanusantos.personalfinancemanager.domain.transaction.installment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    Optional<Installment> findByIdAndTransactionUserId(Long id, Long userId);
}
