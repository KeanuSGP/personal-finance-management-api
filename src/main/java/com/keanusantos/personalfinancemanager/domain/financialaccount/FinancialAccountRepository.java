package com.keanusantos.personalfinancemanager.domain.financialaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, Long> {

    Boolean existsByName(String name);
    Boolean existsByNameAndIdNot(String name, Long id);

}
