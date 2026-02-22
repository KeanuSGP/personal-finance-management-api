package com.keanusantos.personalfinancemanager.domain.user;

import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public User insert(User obj) {
        validateUser(obj);
        return repository.save(obj);
    }

    public User update(Long id, User obj) {
            User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
            updateUserData(entity, obj);
            return repository.save(entity);
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
