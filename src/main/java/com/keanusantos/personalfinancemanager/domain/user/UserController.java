package com.keanusantos.personalfinancemanager.domain.user;

import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<UserResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public UserResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO insert(@Valid @RequestBody User obj) {
        return service.insert(obj);
    }

    @PutMapping(value = "/{id}")
    public UserResponseDTO update(@PathVariable Long id, @RequestBody User obj) {
        return service.update(id, obj);
    }

    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
