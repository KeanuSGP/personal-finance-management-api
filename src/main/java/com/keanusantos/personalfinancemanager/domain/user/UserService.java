package com.keanusantos.personalfinancemanager.domain.user;

import com.keanusantos.personalfinancemanager.domain.user.dto.mapper.UserDTOMapper;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.CreateUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.PutUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

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

        if (repository.existsByEmail(obj.email())) {
            throw new ResourceAlreadyExistsException("Email not available");
        }

        if (repository.existsByName(obj.name())) {
            throw new ResourceAlreadyExistsException("Name not available");
        }

        repository.save(user);
        return UserDTOMapper.toResponse(user);
    }

    public UserResponseDTO update(Long id, PutUserDTO obj) {
        User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));

        if (repository.existsByEmailAndIdNot(obj.email(), entity.getId())) {
            throw new ResourceAlreadyExistsException("Email not available");
        }

        if (repository.existsByNameAndIdNot(obj.name(), entity.getId())) {
            throw new ResourceAlreadyExistsException("Name not available");
        }

        updateUserData(entity, obj);
        repository.save(entity);
        return  UserDTOMapper.toResponse(entity);
    }

    private void updateUserData(User entity, PutUserDTO obj) {
        entity.setName(obj.name());
        entity.setEmail(obj.email());
        entity.setPassword(obj.password());
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }



}
