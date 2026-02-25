package com.keanusantos.personalfinancemanager.domain.payment;

import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(columnDefinition = "TIMESTAMP")
    private Instant moment;

    @ManyToOne
    private FinancialAccount financialAccount;

    @ManyToOne
    private Installment installment;

    public Payment() {
    }

    public Payment(Long id, Instant moment, FinancialAccount financialAccount, Installment installment) {
        if (moment == null) throw new BusinessException("The payment moment must not be null", HttpStatus.BAD_REQUEST);
        if (financialAccount == null) throw new BusinessException("The financial account must not be null", HttpStatus.BAD_REQUEST);
        if (installment == null) throw new BusinessException("The installment must not be null", HttpStatus.BAD_REQUEST);

        this.id = id;
        this.moment = moment.truncatedTo(ChronoUnit.SECONDS);
        this.financialAccount = financialAccount;
        this.installment = installment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {}

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public FinancialAccount getFinancialAccount() {
        return financialAccount;
    }

    public void setFinancialAccount(FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
    }

    public Installment getInstallment() {
        return installment;
    }

    public void setInstallment(Installment installment) {
        this.installment = installment;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
