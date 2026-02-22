package com.keanusantos.personalfinancemanager.domain.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PutInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PatchInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table (name = "installments")
public class Installment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer installmentNumber;

    private Float amount;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column (name = "installment_status")
    private InstallmentStatus status;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "transaction_id")
    @JsonIgnore
    private Transaction transaction;

    public Installment(){};

    public Installment(Long id, Integer installmentNumber, Float amount, LocalDate dueDate, InstallmentStatus status, Transaction transaction) {
        this.id = id;
        this.installmentNumber = installmentNumber;
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
        this.transaction = transaction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInstallmentNumber() {
        return installmentNumber;
    }

    public void setInstallmentNumber(Integer installmentNumber) {
        this.installmentNumber = installmentNumber;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public InstallmentStatus getStatus() {
        return status;
    }

    public void setStatus(InstallmentStatus status) {
        this.status = status;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void putUpdateData(PutInstallmentDTO installment) {
            this.amount = installment.amount();
            this.installmentNumber = installment.installmentNumber();
            this.dueDate = installment.dueDate();
            this.status = installment.status();
    }

    public void parcialUpdateData(PatchInstallmentDTO installment) {
        if (installment.amount() != null) {
            this.amount = installment.amount();
        }
        if (installment.installmentNumber() != null) {
            this.installmentNumber = installment.installmentNumber();
        }
        if (installment.dueDate() != null) {
            this.dueDate = installment.dueDate();
        }
        if (installment.status() != null) {
            this.status = installment.status();
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Installment that = (Installment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
