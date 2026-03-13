package com.keanusantos.personalfinancemanager.domain.counterparty;

import com.keanusantos.personalfinancemanager.domain.category.dto.response.CategoryResponseDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.CreateCounterPartyDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.PutCounterpartyDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.response.CounterPartyResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/counterparties")
public class CounterpartyController {

    @Autowired
    private CounterpartyService service;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<CounterPartyResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/me")
    public List<CounterPartyResponseDTO> findAllByUser() {
        return service.findAllByUserId();
    }

    @GetMapping(value = "/{id}")
    public CounterPartyResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CounterPartyResponseDTO insert(@RequestBody @Valid CreateCounterPartyDTO obj) {
        return service.insert(obj);
    }

    @PutMapping(value = "/{id}")
    public CounterPartyResponseDTO update(@PathVariable Long id, @RequestBody @Valid PutCounterpartyDTO obj) {
        return service.update(id, obj);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


}
