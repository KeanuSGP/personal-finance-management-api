package com.keanusantos.personalfinancemanager.domain.counterparty;

import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CounterPartyService {

    private static final Logger log = LoggerFactory.getLogger(CounterPartyService.class);
    @Autowired
    private CounterPartyRepository repository;

    public List<CounterParty> findAll() {
        return repository.findAll();
    }

    public CounterParty findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public CounterParty insert(CounterParty obj) {
        validateCounterParty(obj);

        return repository.save(obj);
    }

    public CounterParty update(Long id, CounterParty newData) {
        CounterParty counterParty = findById(id);
        updateData(counterParty, newData);
        return repository.save(counterParty);
    }

    public void updateData(CounterParty counterParty, CounterParty newData) {
        validateCounterParty(newData);

        counterParty.setName(newData.getName());
        counterParty.setTaxId(newData.getTaxId());
    }

    public void validateCounterParty(CounterParty obj) {

        String name = obj.getName();
        String taxId = obj.getTaxId();

        if (name.isEmpty()) {
            throw new BusinessException("The name must be filled in", HttpStatus.BAD_REQUEST);
        }

        if (taxId.isEmpty()) {
            throw new BusinessException("The Tax ID must be filled in", HttpStatus.BAD_REQUEST);
        }

        if (repository.existsByName(name)) {
            throw new BusinessException("There is already a counterpart with that name", HttpStatus.CONFLICT);
        }

        if (repository.existsByTaxId(taxId)) {
            throw new BusinessException("A counterpart with that identification already exists", HttpStatus.CONFLICT);
        }
    }


    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }

        repository.deleteById(id);
    }
}
