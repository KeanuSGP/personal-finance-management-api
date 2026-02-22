package com.keanusantos.personalfinancemanager.domain.transaction;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.PatchTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.w3c.dom.css.Counter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table (name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_doc")
    private String doc;

    private LocalDate issueDate;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "transaction_description")
    private String description;

    @ManyToMany
    @JoinTable(name = "transaction_category",
            joinColumns = @JoinColumn(name = "transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installments;

    @ManyToOne
    private CounterParty counterparty;

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

    public void partialUpdateTransaction(Transaction t, PatchTransactionDTO newData, Set<Category> categories, CounterParty counterParty,  FinancialAccount financialAccount, User user) {
        if (newData.categories() != null) {
            if (!categories.isEmpty()) {
                t.setCategories(categories);
            }
        }

        if (newData.doc() != null) {
            t.setDoc(newData.doc());
        }

        if (newData.issueDate() != null) {
            t.setIssueDate(newData.issueDate());
        }

        if (newData.type() != null) {
            t.setType(newData.type());
        }

        if (newData.description() != null) {
            t.setDescription(newData.description());
        }

        if (newData.counterParty() != null) {
            t.setCounterparty(counterParty);
        }

        if (newData.financialAccount() != null) {
            t.setFinancialAccount(financialAccount);
        }

        if (newData.user() != null) {
            t.setUser(user);
        }
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
