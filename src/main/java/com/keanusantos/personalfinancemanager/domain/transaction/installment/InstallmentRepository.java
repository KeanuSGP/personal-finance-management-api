package com.keanusantos.personalfinancemanager.domain.transaction.installment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InstallmentRepository extends JpaRepository<Installment, Long>
{
}
