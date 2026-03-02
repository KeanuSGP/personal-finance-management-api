package com.keanusantos.personalfinancemanager.domain.user;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.config.security.UserDetailsServiceImpl;
import com.keanusantos.personalfinancemanager.domain.role.Role;
import com.keanusantos.personalfinancemanager.domain.role.RoleRepository;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.user.dto.mapper.UserDTOMapper;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.CreateUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.PutUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    public User findByIdEntity(Long id){
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public List<UserResponseDTO> findAll() {
        return repository.findAll().stream().map(UserDTOMapper::toResponse).collect(Collectors.toList());
    }

    public UserResponseDTO findById(Long id) {
       return UserDTOMapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id)));
    }

    public UserResponseDTO insert(CreateUserDTO obj) {
        User user =  UserDTOMapper.toEntity(obj);

        if (repository.existsByName(obj.name())) {
            throw new ResourceAlreadyExistsException("Name not available");
        }

        if (repository.findAll().isEmpty()) {
            List<Role> roles = new ArrayList<>();
            Role role = roleRepository.findByRole(RoleName.ROLE_ADMIN).orElseThrow(() -> new UsernameNotFoundException(RoleName.ROLE_ADMIN.toString()));
            roles.add(role);
            user.setRoles(roles);

        } else {
            List<Role> roles = new ArrayList<>();
            Role role = roleRepository.findByRole(RoleName.ROLE_USER).orElseThrow(() -> new UsernameNotFoundException(RoleName.ROLE_USER.toString()));
            roles.add(role);
            user.setRoles(roles);
        }

        user.setPassword(passwordEncoder.encode(obj.password()));

        repository.save(user);
        return UserDTOMapper.toResponse(user);
    }

    public UserResponseDTO update(Long id, PutUserDTO obj) {
        User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));

        if (repository.existsByNameAndIdNot(obj.name(), entity.getId())) {
            throw new ResourceAlreadyExistsException("Name not available");
        }

        updateUserData(entity, obj);
        repository.save(entity);
        return  UserDTOMapper.toResponse(entity);
    }

    private void updateUserData(User entity, PutUserDTO obj) {
        entity.setName(obj.name());
        entity.setPassword(passwordEncoder.encode(obj.password()));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }



}
