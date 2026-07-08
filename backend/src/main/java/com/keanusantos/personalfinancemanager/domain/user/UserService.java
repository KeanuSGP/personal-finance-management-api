package com.keanusantos.personalfinancemanager.domain.user;

import com.keanusantos.personalfinancemanager.domain.auth.AuthService;
import com.keanusantos.personalfinancemanager.domain.role.Role;
import com.keanusantos.personalfinancemanager.domain.role.RoleRepository;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.user.dto.mapper.UserDTOMapper;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.CreateUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.PutUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AuthService authService;

    public User findByIdEntity(Long id){
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    public List<UserResponseDTO> findAll() {
        User user = authService.getAuthenticatedUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleName.ROLE_ADMIN));
        if (!isAdmin) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
        return repository.findAll().stream().map(UserDTOMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO findById(Long id) {
        User user = authService.getAuthenticatedUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleName.ROLE_ADMIN));
        if (!isAdmin) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
       return UserDTOMapper.toResponse(repository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    @Transactional
    public UserResponseDTO getCurrentUser() {
        User user = authService.getAuthenticatedUser();
        return UserDTOMapper.toResponse(repository.findById(user.getId()).orElseThrow(ResourceNotFoundException::new));
    }

    @Transactional
    public UserResponseDTO insert(CreateUserDTO obj) {
        User user =  UserDTOMapper.toEntity(obj);

        if (repository.existsByName(obj.name())) {
            throw new ResourceAlreadyExistsException("Name not available");
        }
            List<Role> roles = new ArrayList<>();
            Role role = roleRepository.findByRole(RoleName.ROLE_USER).orElseThrow(() -> new UsernameNotFoundException(RoleName.ROLE_USER.toString()));
            roles.add(role);
            user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(obj.password()));

        repository.save(user);
        return UserDTOMapper.toResponse(user);
    }

    @Transactional
    public UserResponseDTO updateByAuthenticatedUser(PutUserDTO obj) {
        User user = authService.getAuthenticatedUser();

        if (repository.existsByNameAndIdNot(obj.name(), user.getId())) {
            throw new ResourceAlreadyExistsException("Name not available");
        }

        updateUserData(user, obj);
        repository.save(user);
        return  UserDTOMapper.toResponse(user);
    }

    @Transactional
    public void delete() {
        User user = authService.getAuthenticatedUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        repository.deleteById(user.getId());
    }

    @Transactional
    private void updateUserData(User entity, PutUserDTO obj) {
        entity.setName(obj.name());
        entity.setPassword(passwordEncoder.encode(obj.password()));
    }



}
