package com.keanusantos.personalfinancemanager.domain.user;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, name = "username")
    private String name;

    @Column(name = "user_password")
    private String password;

    public User() {
    }

    public User(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
