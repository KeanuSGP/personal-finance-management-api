package transaction;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.category.CategoryService;
import com.keanusantos.personalfinancemanager.domain.counterparty.Counterparty;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterpartyService;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountService;
import com.keanusantos.personalfinancemanager.domain.role.Role;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.TransactionRepository;
import com.keanusantos.personalfinancemanager.domain.transaction.TransactionService;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.PutTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
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

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    TransactionRepository repository;

    @Mock
    CounterpartyService counterpartyService;

    @Mock
    FinancialAccountService financialAccountService;

    @Mock
    CategoryService categoryService;

    @InjectMocks
    TransactionService service;


    UserDetailsImpl userDetails;
    User admin;
    Role roleAdmin;
    User user;
    Role roleUser;
    Authentication auth;
    Transaction t;
    Category c1;
    Counterparty counterP1;
    FinancialAccount finAcc;
    Set<Category> categories;

    @BeforeEach
    void setup() {
        admin = new User(1L, "admin", "admin", new ArrayList<>());
        roleAdmin = new Role(1L, RoleName.ROLE_ADMIN);
        admin.getRoles().add(roleAdmin);
        user = new User(2L, "user", "user", new ArrayList<>());
        roleUser = new Role(2L, RoleName.ROLE_USER);
        user.getRoles().add(roleUser);

        c1 = new Category();
        counterP1 = new Counterparty();
        finAcc = new FinancialAccount();
        categories = new HashSet<>();
        categories.add(c1);
        t = new Transaction();
        t.setId(1L);
        t.setCategories(categories);
        t.setCounterparty(counterP1);
        t.setFinancialAccount(finAcc);
        t.setUser(user);
    }

    @Nested
    class findAll {
        @Test
        void shouldReturnAllDatabaseTransactionsWhenAuthenticatedUserIsAdmin() {
            userDetails = new UserDetailsImpl(admin);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            service.findAll();
            verify(repository, times(1)).findAll();
        }

        @Test
        void shouldThrowsBusinessExceptionWhenAuthenticatedUserIsNotAdmin() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            assertThrows(BusinessException.class, () -> service.findAll());
        }
    }

    @Nested
    class findById {
        @Test
        void shouldReturnUserTransactionsWhenUserIsAuthenticated() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.of(t));
            service.findById(1L);
            verify(repository, times(1)).findByIdAndUser_Id(1L, user.getId());
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenUserIsNotOwner() {
            userDetails = new UserDetailsImpl(admin);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findByIdAndUser_Id(1L, admin.getId())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
        }
    }

    @Nested
    class findByFinancialAccountName {
        @Test
        void shouldReturnTransactionsByFinancialAccountNameAndWhenAuthenticatedIsUserOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findAllByFinancialAccountIdAndUserId(1L, user.getId())).thenReturn(List.of(t));
            service.findByFinancialAccountName("F1");
            verify(repository, times(1)).findAllByFinancialAccountIdAndUserId(1L, user.getId());
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenUserIsNotOwnerOrReturnIsEmpty() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findAllByFinancialAccountIdAndUserId(1L, user.getId())).thenReturn(new ArrayList<>());
            assertThrows(ResourceNotFoundException.class, () -> service.findByFinancialAccountName("F1"));
        }
    }

    @Nested
    class update {
        @Test
        void shouldUpdateUserTransactionsWhenUserIsOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.of(t));
            when(repository.existsByDocAndUser_IdAndIdNot(t.getDoc(), user.getId(), t.getId())).thenReturn(false);
            when(counterpartyService.findByIdAndUserId(1L, user.getId())).thenReturn(counterP1);
            when(financialAccountService.findByIdAndUserId(1L, user.getId())).thenReturn(finAcc);
            when(categoryService.findCategoriesByOwner(new HashSet<>(), user.getId())).thenReturn(categories);
            PutTransactionDTO dto = new PutTransactionDTO(t.getDoc(), LocalDate.now(), TransactionType.DEBIT, "nulls", new HashSet<>(), 1L, 1L);
            service.update(1L, dto);
            verify(repository, times(1)).save(t);
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenUserIsNotOwner() {
            userDetails = new UserDetailsImpl(admin);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findByIdAndUser_Id(1L, admin.getId())).thenReturn(Optional.empty());
            PutTransactionDTO dto = new PutTransactionDTO(t.getDoc(), LocalDate.now(), TransactionType.DEBIT, "nulls", new HashSet<>(), 1L, 1L);
            assertThrows(ResourceNotFoundException.class, () -> service.update(1L, dto));
        }
    }

    @Nested
    class delete {
        @Test
        void shouldDeleteTransactionWhenUserIsOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.existsByIdAndUser_Id(1L, user.getId())).thenReturn(true);
            service.delete(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenUserIsNotOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.existsByIdAndUser_Id(1L, user.getId())).thenReturn(false);
            assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        }
    }
}
