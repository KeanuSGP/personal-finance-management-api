package com.keanusantos.personalfinancemanager.domain.financialaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, Long> {

    List<FinancialAccount> findByUserId(Long id);
    Boolean existsByName(String name);

}
