package com.keanusantos.personalfinancemanager.domain.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsById(Long id);
    boolean existsByIdAndUserId(Long id, Long userId);
    List<Payment> findAllByUserId(Long userId);
    Optional<Payment> findByIdAndUserId(Long id, Long userId);
}
