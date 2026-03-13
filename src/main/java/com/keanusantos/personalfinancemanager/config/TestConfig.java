package com.keanusantos.personalfinancemanager.config;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.category.CategoryRepository;
import com.keanusantos.personalfinancemanager.domain.counterparty.Counterparty;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterpartyRepository;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountRepository;
import com.keanusantos.personalfinancemanager.domain.payment.Payment;
import com.keanusantos.personalfinancemanager.domain.payment.PaymentRepository;
import com.keanusantos.personalfinancemanager.domain.role.Role;
import com.keanusantos.personalfinancemanager.domain.role.RoleRepository;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.TransactionRepository;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.InstallmentRepository;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDate;
import java.util.*;


@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FinancialAccountRepository finAccRepository;

    @Autowired
    private CounterpartyRepository counterPartyRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        Role roleUser = new Role(null, RoleName.ROLE_USER);
        Role roleAdmin = new Role(null, RoleName.ROLE_ADMIN);

        roleRepository.saveAll(Arrays.asList(roleUser, roleAdmin));

        User user = new User(null, "user", passwordEncoder.encode("user"), new ArrayList<>());
        user.getRoles().add(roleUser);
        User admin = new User(null, "admin", passwordEncoder.encode("admin"), new ArrayList<>());
        admin.getRoles().add(roleAdmin);

        userRepository.saveAll(Arrays.asList(user, admin));

        FinancialAccount finAcc1 = new FinancialAccount(null, "Account 1", 500.0f, user);
        FinancialAccount finAcc2 = new FinancialAccount(null, "Account 2", 200f, user);
        FinancialAccount finAcc3 = new FinancialAccount(null, "Account 3", 5000f, user);

        FinancialAccount finAcc4 = new FinancialAccount(null, "Account 4", 10f, user);
        FinancialAccount finAcc5 = new FinancialAccount(null, "Account 5", 5000f, user);
        FinancialAccount finAcc6 = new FinancialAccount(null, "Account 6", 10000f, user);

        finAccRepository.saveAll(Arrays.asList(finAcc1, finAcc2, finAcc3, finAcc4, finAcc5, finAcc6));

        Counterparty counterP1 = new Counterparty(null, "CounterParty1", "12345678912345", user);
        Counterparty counterP2 = new Counterparty(null, "CounterParty2", "987654399", user);

        counterPartyRepository.saveAll(Arrays.asList(counterP1, counterP2));

        Category c1 = new Category(null, "Alimento", "ffffff", user);
        Category c2 = new Category(null, "Roupa", "000000", user);
        Category c3 = new Category(null, "Despesas gerais", "555555", user);

        categoryRepository.saveAll(Arrays.asList(c1, c2, c3));

        Set<Category> categories = new HashSet<>();

        categories.add(c1);
        categories.add(c2);
        categories.add(c3);

        Transaction t1 = new Transaction(null, "12311", LocalDate.now(), TransactionType.DEBIT, "ssss", categories, new ArrayList<>(), counterP1, finAcc1, user);


        Installment installment1 = new Installment(null, 1, 200.0f, LocalDate.now(), InstallmentStatus.PENDING, t1, null);
        Installment installment2 = new Installment(null, 2, 200.0f, LocalDate.now(), InstallmentStatus.PENDING, t1, null);
        Installment installment3 = new Installment(null, 3, 200.0f, LocalDate.now(), InstallmentStatus.PENDING, t1, null);

        t1.addInstallment(installment1);
        t1.addInstallment(installment2);
        t1.addInstallment(installment3);


        transactionRepository.save(t1);

        Payment payment = installment1.pay(finAcc3, user);
        payment.setUser(user);
        paymentRepository.save(payment);
        installmentRepository.save(installment1);
        finAccRepository.save(finAcc3);


    }
}
