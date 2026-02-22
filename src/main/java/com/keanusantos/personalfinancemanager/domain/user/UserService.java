package com.keanusantos.personalfinancemanager.domain.user;

import com.keanusantos.personalfinancemanager.domain.user.dto.mapper.UserDTOMapper;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
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

    public List<UserResponseDTO> findAll() {
        return repository.findAll().stream().map(UserDTOMapper::toResponse).collect(Collectors.toList());
    }

    public UserResponseDTO findById(Long id) {
       return UserDTOMapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id)));
    }

    public UserResponseDTO insert(User obj) {
        validateUser(obj);
        repository.save(obj);
        return UserDTOMapper.toResponse(obj);
    }

    public UserResponseDTO update(Long id, User obj) {
            User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
            updateUserData(entity, obj);
            repository.save(entity);
            return  UserDTOMapper.toResponse(entity);
    }

    private void updateUserData(User entity, User obj) {
        validateUser(obj);

        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
        entity.setPassword(obj.getPassword());
    }

    public void validateUser(User obj) {
        if (repository.existsByName(obj.getName())) {
            throw new BusinessException("Name already in use", HttpStatus.CONFLICT);
        }

        if (repository.existsByEmail(obj.getEmail())) {
            throw new BusinessException("Email already in use", HttpStatus.CONFLICT);
        }
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }



}
