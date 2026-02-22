package com.keanusantos.personalfinancemanager.domain.financialaccount;

import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.CreateAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.PutAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.response.FinancialAccountResponseDTO;
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
    public List<FinancialAccountResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public FinancialAccountResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialAccountResponseDTO insert(@Valid @RequestBody CreateAccountDTO obj) {
        return service.insert(obj);
    }

    @PutMapping(value = "/{id}")
    public FinancialAccountResponseDTO update(@PathVariable Long id, @RequestBody @Valid PutAccountDTO obj) {
        return service.update(id, obj);
    }

    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
