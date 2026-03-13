package com.keanusantos.personalfinancemanager.domain.financialaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialAccountRepository extends JpaRepository<FinancialAccount, Long> {

    Optional<FinancialAccount> findByIdAndUserId(Long id, Long userId);
    List<FinancialAccount> findAllByUserId(Long userId);
    Boolean existsByNameAndUserId(String name, Long id);
    Boolean existsByNameAndUserIdAndIdNot(String name, Long userId, Long id);
    Optional<FinancialAccount> findByNameAndUserId(String name, Long id);

}
