package com.keanusantos.personalfinancemanager.domain.transaction;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table (name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "transaction_doc")
    @NotEmpty(message = "The document cannot be empty")
    private String doc;

    @NotNull(message = "The date cannot be null")
    private LocalDate issueDate;

    @NotNull(message = "Define the type for transaction")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType type;

    @Column(name = "transaction_description")
    private String description;

    @ManyToMany
    @JoinTable(name = "transaction_category",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @NotEmpty(message = "Transaction must have at least one category")
    private Set<Category> categories;

    @NotNull(message = "The account cannot be created without an installment")
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installments;

    @NotNull(message = "Define the counterparty for the transaction")
    @ManyToOne
    private CounterParty counterparty;

    @NotNull(message = "Define the transaction account")
    @ManyToOne
    private FinancialAccount financialAccount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Transaction(){};

    public Transaction(Long id, String doc, LocalDate issueDate, TransactionType type, String description, Set<Category> categories, List<Installment> installments, CounterParty counterparty, FinancialAccount financialAccount, User user) {
        this.id = id;
        this.doc = doc;
        this.issueDate = issueDate;
        this.type = type;
        this.description = description;
        this.categories = categories != null ? new HashSet<>(categories): new HashSet<>();
        this.installments = installments;
        this.counterparty = counterparty;
        this.financialAccount = financialAccount;
        this.user = user;
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

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
