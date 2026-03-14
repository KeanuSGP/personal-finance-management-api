package com.keanusantos.personalfinancemanager.domain.user;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    public User findByIdEntity(Long id){
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    public List<UserResponseDTO> findAll() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User entity = user.getUser();
        if (entity == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        boolean isAdmin = entity.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleName.ROLE_ADMIN));
        if (!isAdmin) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
        return repository.findAll().stream().map(UserDTOMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDTO findById(Long id) {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User entity = user.getUser();
        if (entity == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        boolean isAdmin = entity.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleName.ROLE_ADMIN));
        if (!isAdmin) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
       return UserDTOMapper.toResponse(repository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    @Transactional
    public UserResponseDTO getCurrentUser() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UserDTOMapper.toResponse(repository.findById(user.getUser().getId()).orElseThrow(ResourceNotFoundException::new));
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
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User entity = user.getUser();

        if (repository.existsByNameAndIdNot(obj.name(), entity.getId())) {
            throw new ResourceAlreadyExistsException("Name not available");
        }

        updateUserData(entity, obj);
        repository.save(entity);
        return  UserDTOMapper.toResponse(entity);
    }

    @Transactional
    public void delete() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User entity = user.getUser();
        if (entity == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        repository.deleteById(entity.getId());
    }

    @Transactional
    private void updateUserData(User entity, PutUserDTO obj) {
        entity.setName(obj.name());
        entity.setPassword(passwordEncoder.encode(obj.password()));
    }



}
