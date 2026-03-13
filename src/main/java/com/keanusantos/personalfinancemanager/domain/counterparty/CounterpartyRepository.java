package com.keanusantos.personalfinancemanager.domain.counterparty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CounterpartyRepository extends JpaRepository<Counterparty, Long> {
    boolean existsByIdAndUserId(Long id, Long userId);
    boolean existsByNameAndUserId(String name, Long id);
    boolean existsByTaxIdAndUserId(String taxId, Long id);
    boolean existsByNameAndUserIdAndIdNot(String name, Long id, Long userId);
    boolean existsByTaxIdAndUserIdAndIdNot(String taxId, Long id, Long userId);
    Optional<Counterparty> findByIdAndUserId(Long id, Long userId);
    List<Counterparty> findAllByUserId(Long userId);
}
