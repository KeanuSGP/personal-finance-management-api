package com.keanusantos.personalfinancemanager.config;

import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountRepository;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialAccountRepository finAccRepository;


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
    }
}
