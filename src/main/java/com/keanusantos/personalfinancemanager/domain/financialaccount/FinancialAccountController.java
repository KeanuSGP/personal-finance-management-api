package com.keanusantos.personalfinancemanager.domain.financialaccount;

import com.keanusantos.personalfinancemanager.domain.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/accounts")
public class FinancialAccountController {

    @Autowired
    private FinancialAccountService service;

    @GetMapping
    public List<FinancialAccount> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public FinancialAccount findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/user/{id}")
    public List<FinancialAccount> findByUserId(@PathVariable Long id) {
        return service.findByUserId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialAccount insert(@Valid @RequestBody FinancialAccount obj) {
        return service.insert(obj);
    }

    @PutMapping(value = "/{id}")
    public FinancialAccount update(@PathVariable Long id, @RequestBody FinancialAccount obj) {
        return service.update(id, obj);
    }

    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
