package com.keanusantos.personalfinancemanager.domain.user;

import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import com.keanusantos.personalfinancemanager.domain.user.exception.EmailAlreadyExistsException;
import com.keanusantos.personalfinancemanager.domain.user.exception.NameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<User> user = repository.findById(id);
        return user.orElseThrow(() -> new ResourceNotFoundException(id));
    }


    public User insert(User obj) {
        if (repository.existsByName(obj.getName())) {
            throw new NameAlreadyExistsException();
        }

        if (repository.existsByEmail(obj.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        return repository.save(obj);
    }

    private String formatErrorMessage(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for(String a: list) {
            sb.append(a);
        }
        return sb.toString();
    }

    public User update(Long id, User obj) {
            User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
            updateUserData(entity, obj);
            return repository.save(entity);
    }

    private void updateUserData(User entity, User obj) {
        entity.setName(obj.getName());
        entity.setEmail(obj.getEmail());
        entity.setPassword(obj.getPassword());
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }


}
