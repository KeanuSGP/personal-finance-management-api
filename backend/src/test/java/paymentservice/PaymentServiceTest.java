package paymentservice;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.counterparty.Counterparty;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountService;
import com.keanusantos.personalfinancemanager.domain.payment.Payment;
import com.keanusantos.personalfinancemanager.domain.payment.PaymentRepository;
import com.keanusantos.personalfinancemanager.domain.payment.PaymentService;
import com.keanusantos.personalfinancemanager.domain.payment.dto.request.CreatePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.role.Role;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.InstallmentRepository;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    UserDetailsImpl userDetails;
    User user;
    User admin;
    Role roleAdmin;
    FinancialAccount finAcc1;
    Counterparty counterP1;
    Category category;
    Transaction t1;
    Installment i1;
    CreatePaymentDTO dto;
    Authentication auth;
    Payment payment;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test", "test@test.com", new ArrayList<>());
        admin = new User(1L, "admin", "admin", new ArrayList<>());
        roleAdmin = new Role(1L, RoleName.ROLE_ADMIN);
        admin.getRoles().add(roleAdmin);
        finAcc1 = new FinancialAccount(1L, "Account 1", 1000f, user);
        counterP1 = new Counterparty(1L, "CounterParty1", "12345678912345", user);
        category = new Category(1L, "ALIMENTO", "FFFFFF", user);
        Set<Category> categories = new HashSet<Category>();
        categories.add(category);
        String t1Description = "Criação da primeira transação para o teste automatizado de PaymentService";
        t1 = new Transaction(1L, "NF1238888", LocalDate.now(), TransactionType.DEBIT, t1Description, categories, new ArrayList<>(), counterP1, finAcc1, user);
        i1 = new Installment(1L, 1, 200.0f, LocalDate.now(), InstallmentStatus.PENDING, t1, null);
        t1.addInstallment(i1);
        dto = new CreatePaymentDTO(1L);
        payment = new Payment(1L, Instant.now(), finAcc1, i1, user);

    }

    @Nested
    class findById {
        @Test
        void shouldReturnPaymentWhenUserIsOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(paymentRepository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.of(payment));
            paymentService.findById(1L);
            verify(paymentRepository, times(1)).findByIdAndUserId(1L, user.getId());
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenUserIsNotOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(paymentRepository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> paymentService.findById(1L));
        }
    }

    @Nested
    class findAll {
        @Test
        void shouldReturnPaymentsWhenUserIsAdmin() {
            userDetails = new UserDetailsImpl(admin);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            paymentService.findAll();
            verify(paymentRepository, times(1)).findAll();
        }

        @Test
        void shouldThrowsBusinessExceptionWhenUserIsNotAdmin() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            assertThrows(BusinessException.class, () -> paymentService.findAll());
        }
    }

    @Nested
    class insert {
        @Test
        void shouldCreatePaymentWhenUserIsFinancialAccountAndInstallmentOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);

            when(financialAccountService.findByIdAndUserId(1L, user.getId())).thenReturn(finAcc1);
            when(installmentRepository.findByIdAndTransactionUserId(1L, user.getId())).thenReturn(Optional.of(i1));

            paymentService.insert(1L, dto);

            assertEquals(800f, finAcc1.getBalance());
            assertEquals(InstallmentStatus.PAID, i1.getStatus());
            verify(paymentRepository, times(1)).save(any(Payment.class));
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenUserIsNotFinancialAccountOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(financialAccountService.findByIdAndUserId(1L, user.getId())).thenReturn(null);
            assertThrows(ResourceNotFoundException.class, () -> paymentService.insert(1L, dto));
        }

        @Test
        void shouldTrowsResourceNotFoundExceptionWhenUserIsNotOwnerOfInstallmentTransaction() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(installmentRepository.findByIdAndTransactionUserId(1L, user.getId())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> paymentService.insert(1L, dto));
        }
    }

    @Nested
    class delete {
        @Test
        void shouldDeletePaymentWhenUserIsFinancialAccountAndInstallmentOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(paymentRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(payment));
            when(installmentRepository.findByIdAndTransactionUserId(1L, 1L)).thenReturn(Optional.of(i1));
            i1.setStatus(InstallmentStatus.PAID);
            paymentService.delete(1L);
            verify(paymentRepository, times(1)).deleteById(1L);
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenUserIsNotPaymentOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(paymentRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> paymentService.delete(1L));
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenUserIsNotInstallmentOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(paymentRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(payment));
            when(installmentRepository.findByIdAndTransactionUserId(1L, 1L)).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> paymentService.delete(1L));
        }
    }



}
