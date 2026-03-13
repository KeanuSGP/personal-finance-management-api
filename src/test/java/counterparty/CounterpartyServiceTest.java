package counterparty;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.counterparty.Counterparty;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterpartyRepository;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterpartyService;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.PutCounterpartyDTO;
import com.keanusantos.personalfinancemanager.domain.role.Role;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CounterpartyServiceTest {

    @Mock
    CounterpartyRepository repository;

    @InjectMocks
    CounterpartyService counterPartyService;

    Role roleAdmin = new Role(1L, RoleName.ROLE_ADMIN);
    User admin;
    User user;
    User user2;
    Role roleUser = new Role(2L, RoleName.ROLE_USER);
    UserDetailsImpl userDetails;
    Authentication auth;
    Counterparty c1;
    Counterparty c2;
    Counterparty c3;
    List<Counterparty> counterparties = new ArrayList<>();


    @BeforeEach
    void setup() {
        admin = new User(1L, "admin", "123456", new ArrayList<>());
        admin.getRoles().add(roleAdmin);
        user = new User(2L, "user", "123456", new ArrayList<>());
        user2 = new User(3L, "user2", "123456", new ArrayList<>());
        user.getRoles().add(roleUser);
        user2.getRoles().add(roleUser);
//        auth = new UsernamePasswordAuthenticationToken("admin", "123456");
//        SecurityContextHolder.getContext().setAuthentication(auth);

        c1 = new Counterparty(1L, "c1", "123456789", user);
        c2 = new Counterparty(2L, "c2", "234567891", user);
        c3 = new Counterparty(3L, "c3", "345678912", user);
        counterparties.addAll(Arrays.asList(c1, c2, c3));
    }

    @Nested
    class findAll {
        @Test
        void shouldReturnAllDatabaseCounterpartiesWhenUserIsRoleAdmin() {
            userDetails = new UserDetailsImpl(admin);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findAll()).thenReturn(counterparties);
            counterPartyService.findAll();
            verify(repository, times(1)).findAll();
        }

        @Test
        void shouldThrowsBusinessExceptionWhenUserIsNotRoleAdmin() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            assertThrows(BusinessException.class, () -> counterPartyService.findAll());
        }
    }

    @Nested
    class findById {
        @Test
        void shouldReturnCounterpartyWhenUserIsOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.of(c1));
            counterPartyService.findById(1L);
            verify(repository, times(1)).findByIdAndUserId(1L, user.getId());
        }

        @Test
        void shouldThrowsBusinessExceptionWhenUserIsNotOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            assertThrows(BusinessException.class, () -> counterPartyService.findByIdAndUserId(1L, user2.getId()));
        }
    }

    @Nested
    class update {
        @Test
        void shouldUpdateCounterPartyWhenUserIsAuthenticated() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(c1));
            PutCounterpartyDTO dto = new PutCounterpartyDTO("C4", "123456789");
            when(repository.existsByNameAndUserIdAndIdNot(dto.name(), 2L, c1.getId())).thenReturn(false);
            when(repository.existsByTaxIdAndUserIdAndIdNot(dto.taxId(), 2L, c1.getId())).thenReturn(false);
            counterPartyService.update(1L, dto);
            verify(repository, times(1)).save(any(Counterparty.class));
        }

        @Test
        void shouldThrowsBusinessExceptionWhenUserIsNotOwner() {
            userDetails = new UserDetailsImpl(user2);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.findByIdAndUserId(1L, 3L)).thenReturn(Optional.empty());
            assertThrows(BusinessException.class, () -> counterPartyService.findByIdAndUserId(1L, 3L));
        }
    }

    @Nested
    class delete {
        @Test
        void shouldDeleteCounterpartyWhenAuthenticatedUserIsOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.existsByIdAndUserId(1L, 2L)).thenReturn(true);
            counterPartyService.delete(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        void shouldThrowsBusinessExceptionWhenAuthenticatedUserIsNotOwner() {
            userDetails = new UserDetailsImpl(user2);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(repository.existsByIdAndUserId(1L, user2.getId())).thenReturn(false);
            assertThrows(BusinessException.class, () -> counterPartyService.delete(1L));
        }
    }
}
