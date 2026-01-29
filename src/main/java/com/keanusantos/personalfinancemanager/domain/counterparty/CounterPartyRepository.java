package com.keanusantos.personalfinancemanager.domain.counterparty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounterPartyRepository extends JpaRepository<CounterParty, Long> {
    boolean existsByName(String name);
    boolean existsByTaxId(String taxId);
}
