package paymentservicetest;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountService;
import com.keanusantos.personalfinancemanager.domain.payment.Payment;
import com.keanusantos.personalfinancemanager.domain.payment.PaymentRepository;
import com.keanusantos.personalfinancemanager.domain.payment.PaymentService;
import com.keanusantos.personalfinancemanager.domain.payment.dto.request.CreatePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.payment.dto.response.ResponsePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.InstallmentRepository;
import com.keanusantos.personalfinancemanager.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    FinancialAccountService financialAccountService;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    InstallmentRepository installmentRepository;

    @InjectMocks
    PaymentService paymentService;

    User user;
    FinancialAccount finAcc1;
    CounterParty counterP1;
    Category category;
    Transaction t1;
    Installment i1;
    CreatePaymentDTO dto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test", "test@test.com", "123456");
        finAcc1 = new FinancialAccount(1L, "Account 1", 1000f, user);
        counterP1 = new CounterParty(1L, "CounterParty1", "12345678912345", user);
        category = new Category(1L, "ALIMENTO", "FFFFFF", user);
        Set<Category> categories = new HashSet<Category>();
        categories.add(category);
        String t1Description = "Criação da primeira transação para o teste automatizado de PaymentService";
        t1 = new Transaction(1L, "NF1238888", LocalDate.now(), TransactionType.DEBIT, t1Description, categories, new ArrayList<>(), counterP1, finAcc1, user);
        i1 = new Installment(1L, 1, 200.0f, LocalDate.now(), InstallmentStatus.PENDING, t1);
        t1.addInstallment(i1);
        dto = new CreatePaymentDTO(1L);
    }


    @Test
    public void createPayment() {

        when(financialAccountService.findByIdEntity(1L)).thenReturn(finAcc1);
        when(installmentRepository.findById(1L)).thenReturn(Optional.of(i1));

        paymentService.insert(1L, dto);

        assertEquals(800f, finAcc1.getBalance());
        assertEquals(InstallmentStatus.PAID, i1.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

}
