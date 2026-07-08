package category;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.category.CategoryRepository;
import com.keanusantos.personalfinancemanager.domain.category.CategoryService;
import com.keanusantos.personalfinancemanager.domain.category.dto.mapper.CategoryDTOMapper;
import com.keanusantos.personalfinancemanager.domain.category.dto.request.PutCategoryDTO;
import com.keanusantos.personalfinancemanager.domain.category.dto.response.CategoryResponseDTO;
import com.keanusantos.personalfinancemanager.domain.role.Role;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    CategoryDTOMapper categoryDTOMapper = mock(CategoryDTOMapper.class);

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

    UserDetailsImpl userDetails;
    User admin;
    Role adminRole;
    User user;
    Role  userRole;
    Authentication auth;
    Category c1;

    @BeforeEach
    void setup(){
        adminRole = new Role(1L, RoleName.ROLE_ADMIN);
        admin = new User(1L, "admin", "admin", null);
        admin.getRoles().add(adminRole);
        userRole = new Role(2L, RoleName.ROLE_USER);
        user = new User(2L, "user", "user", null);
        user.getRoles().add(userRole);
        c1 = new Category(1L, "c1", "111111", user);

    }

    @Nested
    class findAll {
        @Test
        void shouldReturnAllCategoriesWhenAuthenticatedUserIsAdmin() {
            userDetails = new UserDetailsImpl(admin);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            categoryService.findAll();
            verify(categoryRepository, times(1)).findAll();
        }

        @Test
        void shouldThrowsBusinessExceptionWhenAuthenticatedUserIsNotAdmin() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            assertThrows(BusinessException.class, () -> categoryService.findAll());
        }
    }

    @Nested
    class findById {
        @Test
        void shouldReturnCategoryWhenAuthenticatedUserIsOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(categoryRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.of(c1));
            categoryService.findById(1L);
            verify(categoryRepository, times(1)).findByIdAndUser_Id(1L, user.getId());
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenAuthenticatedUserIsNotOwner() {
            userDetails = new UserDetailsImpl(admin);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(categoryRepository.findByIdAndUser_Id(1L, admin.getId())).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(1L));
        }
    }

    @Nested
    class findCategoriesByOwner {
        @Test
        void shouldReturnAllCategoriesWhenAuthenticatedUserIsOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            Set<Category> categories = new HashSet<>();
            categories.add(c1);
            when(categoryRepository.findAllByIdInAndUser_id(new HashSet<>(), user.getId())).thenReturn(categories);
            categoryService.findCategoriesByOwner(new HashSet<>(),  user.getId());
            verify(categoryRepository, times(1)).findAllByIdInAndUser_id(new HashSet<>(), user.getId());
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenCategoriesDoesNotExistsOrUserIsNotOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(categoryRepository.findAllByIdInAndUser_id(new HashSet<>(), user.getId())).thenReturn(new HashSet<>());
            assertThrows(ResourceNotFoundException.class, () -> categoryService.findCategoriesByOwner(new HashSet<>(), user.getId()));
        }
    }

    @Nested
    class update {
        @Test
        void shouldUpdateCategoryWhenAuthenticatedUserIsOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(categoryRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.of(c1));
            PutCategoryDTO dto = new PutCategoryDTO("c1", "111111");
            categoryService.update(1L, dto);
            verify(categoryRepository, times(1)).save(c1);
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenAuthenticatedUserIsNotOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(categoryRepository.findByIdAndUser_Id(1L, user.getId())).thenReturn(Optional.empty());
            PutCategoryDTO dto = new PutCategoryDTO("c1", "111111");
            assertThrows(ResourceNotFoundException.class, () -> categoryService.update(1L, dto));
        }
    }

    @Nested
    class delete {
        @Test
        void shouldDeleteCategoryWhenAuthenticatedUserIsOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(categoryRepository.existsByIdAndUserId(1L, user.getId())).thenReturn(true);
            categoryService.delete(1L);
            verify(categoryRepository, times(1)).deleteById(1L);
        }

        @Test
        void shouldThrowsResourceNotFoundExceptionWhenAuthenticatedUserIsNotOwner() {
            userDetails = new UserDetailsImpl(user);
            auth = new UsernamePasswordAuthenticationToken(userDetails, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(categoryRepository.existsByIdAndUserId(1L, user.getId())).thenReturn(false);
            assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(1L));
        }
    }
}
