package com.keanusantos.personalfinancemanager.config;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.category.CategoryRepository;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterPartyRepository;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountRepository;
import com.keanusantos.personalfinancemanager.domain.transaction.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.TransactionRepository;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


import java.time.LocalDate;
import java.util.*;


@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialAccountRepository finAccRepository;

    @Autowired
    private CounterPartyRepository counterPartyRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public void run(String... args) throws Exception {
        User user1 = new User(null, "Keanu", "keanu@test.com", "123456");
        User user2 = new User(null, "Santos", "Santos@test.com", "123456");

        userRepository.saveAll(Arrays.asList(user1, user2));

        FinancialAccount finAcc1 = new FinancialAccount(null, "Account 1", 0.0f, user1);
        FinancialAccount finAcc2 = new FinancialAccount(null, "Account 2", 200f, user1);
        FinancialAccount finAcc3 = new FinancialAccount(null, "Account 3", 5000f, user1);

        FinancialAccount finAcc4 = new FinancialAccount(null, "Account 4", 10f, user2);
        FinancialAccount finAcc5 = new FinancialAccount(null, "Account 5", 5000f, user2);
        FinancialAccount finAcc6 = new FinancialAccount(null, "Account 6", 10000f, user2);

        finAccRepository.saveAll(Arrays.asList(finAcc1, finAcc2, finAcc3, finAcc4, finAcc5, finAcc6));

        CounterParty counterP1 = new CounterParty(null, "CounterParty1", "12345678912345");
        CounterParty counterP2 = new CounterParty(null, "CounterParty2", "9876543");

        counterPartyRepository.saveAll(Arrays.asList(counterP1, counterP2));

        Transaction t1 = new Transaction(null, "12311", LocalDate.now(), TransactionType.DEBIT, "ssss", new ArrayList<>(), counterP1, finAcc1);


        Installment installment1 = new Installment(null, 1, 200.0f, LocalDate.now(), InstallmentStatus.PENDING, t1);
        Installment installment2 = new Installment(null, 2, 200.0f, LocalDate.now(), InstallmentStatus.PENDING, t1);
        Installment installment3 = new Installment(null, 3, 200.0f, LocalDate.now(), InstallmentStatus.PENDING, t1);

        t1.addInstallment(installment1);
        t1.addInstallment(installment2);
        t1.addInstallment(installment3);

        transactionRepository.save(t1);

        Category c1 = new Category(null, "Alimento", "ffffff");
        Category c2 = new Category(null, "Roupa", "000000");
        Category c3 = new Category(null, "Despesas gerais", "555555");

        categoryRepository.saveAll(Arrays.asList(c1, c2, c3));
    }
}
