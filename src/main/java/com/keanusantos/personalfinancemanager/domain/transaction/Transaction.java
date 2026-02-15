package com.keanusantos.personalfinancemanager.domain.transaction;

import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotEmpty(message = "The document cannot be empty")
    private String doc;

    @NotNull(message = "The date cannot be null")
    private LocalDate issueDate;

    @NotNull(message = "Define the type for transaction")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String description;

    @NotNull(message = "The account cannot be created without an installment")
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installments;

    @NotNull(message = "Define the counterparty for the transaction")
    @ManyToOne
    private CounterParty counterparty;

    @NotNull(message = "Define the transaction account")
    @ManyToOne
    private FinancialAccount financialAccount;
    // inserir atributo "category" quando a entidade for criada

    public Transaction(){};

    public Transaction(Long id, String doc, LocalDate issueDate, TransactionType type, String description, List<Installment> installments, CounterParty counterparty, FinancialAccount financialAccount) {
        this.id = id;
        this.doc = doc;
        this.issueDate = issueDate;
        this.type = type;
        this.description = description;
        this.installments = installments;
        this.counterparty = counterparty;
        this.financialAccount = financialAccount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Installment> getInstallments() {
        return installments;
    }

    public void setInstallments(List<Installment> installments) {
        this.installments = installments;
    }


    public CounterParty getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(CounterParty counterparty) {
        this.counterparty = counterparty;
    }

    public FinancialAccount getFinancialAccount() {
        return financialAccount;
    }

    public void setFinancialAccount(FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
    }

    public void addInstallment(Installment installment) {
        installment.setTransaction(this);
        installments.add(installment);
    }

    public void addListOfInstallments(List<Installment> list) {
        list.forEach(installment -> {
            installment.setTransaction(this);
            installments.add(installment);
        });
    }

    public void clearInstallmentList() {
        installments.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) && Objects.equals(doc, that.doc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, doc);
    }
}
