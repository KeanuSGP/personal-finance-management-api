package com.keanusantos.personalfinancemanager.domain.financialaccount;

import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Entity
@Table(name = "financial_accounts")
public class FinancialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "account_name")
    private String name;

    private Float balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public FinancialAccount(){
    };

    public FinancialAccount(Long id, String name, Float balance, User user) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void debit(Float amount) {
        validateBalance(amount);
        this.balance -= amount;
    }

    public void credit(Float amount) {
        validateBalance(amount);
        this.balance += amount;
    }

    public void validateBalance(Float amount) {
        if (balance < amount) {
            throw new BusinessException("Insufficient balance", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FinancialAccount that = (FinancialAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
