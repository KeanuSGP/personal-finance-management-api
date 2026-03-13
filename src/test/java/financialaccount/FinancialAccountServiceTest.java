package financialaccount;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountRepository;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountService;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.CreateAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.PutAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.response.FinancialAccountResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FinancialAccountServiceTest {

    @Mock
    UserService userService;

    @Mock
    FinancialAccountRepository finAccRepository;

    @InjectMocks
    FinancialAccountService finAccService;

    FinancialAccount finAcc;
    FinancialAccount finAcc2;
    UserDetailsImpl invader;
    User user;
    User user2;
    Authentication auth;

//    private final List<FinancialAccount> userAccounts = new ArrayList<>();

    @BeforeEach
    void setup() {
        user = new User(1L, "User", "123456", new ArrayList<>());
        user2 = new User(1L, "User", "123456", new ArrayList<>());
        invader = new UserDetailsImpl(user);
        auth = new UsernamePasswordAuthenticationToken(invader, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);

        finAcc =  new FinancialAccount(1L, "Account 1", 1000f, user);
        finAcc2 =  new FinancialAccount(2L, "Account 2", 1000f, user);

    }

    @Nested
   class findById {
    @Test
        void shouldReturnFinancialAccountWhenUserIsOwner() {
            when(finAccRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(finAcc));
            FinancialAccountResponseDTO fin = finAccService.findById(1L);
            assertThat(fin).isNotNull().extracting(FinancialAccountResponseDTO::id).isEqualTo(1L);
        }

    @Test
    void shouldThrowsBusinessExceptionWhenUserIsNotOwner() {
            when(finAccRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.empty());
            assertThrows(BusinessException.class, () -> finAccService.findByIdAndUserId(1L, 2L));
        }
    }

    @Nested
    class insert {

        @Test
        void shouldCreateAccountAndReturnFinancialAccountResponseDTO() {
            CreateAccountDTO create = new CreateAccountDTO("Account2", 200f, 1L);
            when(finAccRepository.existsByNameAndUserId("Account2", 1L)).thenReturn(false);
            FinancialAccountResponseDTO fin = finAccService.insert(create);
            assertThat(fin).isNotNull();
            verify(finAccRepository, Mockito.times(1)).save(any());
        }

        @Test
        void shouldReturnResourceAlreadyExistsException() {
            CreateAccountDTO create = new CreateAccountDTO("Account2", 200f, 1L);
            when(finAccRepository.existsByNameAndUserId("Account2", 1L)).thenReturn(true);
            assertThrows(ResourceAlreadyExistsException.class, () -> finAccService.insert(create));
        }
    }

    @Nested
    class update {
        @Test
        void shouldUpdateAccountAndReturnFinancialAccountResponseDTO() {
            when(finAccRepository.findByIdAndUserId(2L, 1L)).thenReturn(Optional.of(finAcc));
            when(finAccRepository.existsByNameAndUserIdAndIdNot("Account2", 1L, 2L)).thenReturn(false);
            PutAccountDTO update = new PutAccountDTO("Account2", 200f, 1L);
            FinancialAccountResponseDTO dto = finAccService.update(2L, update);
            assertThat(dto).isNotNull();
            verify(finAccRepository, Mockito.times(1)).save(any());
        }

        @Test
        void shouldThrowsResourceAlreadyExistsExceptionWhenAccountNameExistsForUser() {
            PutAccountDTO update = new PutAccountDTO("Account2", 200f, 1L);
            when(finAccRepository.existsByNameAndUserIdAndIdNot("Account2", 1L, 2L)).thenReturn(true);
            assertThrows(ResourceAlreadyExistsException.class, () -> finAccService.updateAccount(finAcc2, update));
        }
    }

    @Nested
    class delete {
        @Test
        void shouldDeleteAccountWhenUserIsOwner() {
            when(finAccRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(finAcc));
            when(finAccRepository.findById(1L)).thenReturn(Optional.of(finAcc));
            finAccService.delete(1L);
            verify(finAccRepository, Mockito.times(1)).deleteById(any());
        }

        @Test
        void shouldThrowsBusinessExceptionWhenUserIsNotOwner() {
            when(finAccRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
            assertThrows(BusinessException.class, () -> finAccService.delete(1L));
        }
    }
}
