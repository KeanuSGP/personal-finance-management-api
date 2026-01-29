package com.keanusantos.personalfinancemanager.domain.counterparty;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/counterparties")
public class CounterPartyController {

    @Autowired
    private CounterPartyService service;

    @GetMapping
    public List<CounterParty> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public CounterParty findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CounterParty insert(@RequestBody CounterParty obj) {
        return service.insert(obj);
    }

    @PutMapping(value = "/{id}")
    public CounterParty update(@PathVariable Long id, @RequestBody CounterParty obj) {
        return service.update(id, obj);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }


}
