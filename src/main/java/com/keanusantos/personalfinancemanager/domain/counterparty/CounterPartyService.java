package com.keanusantos.personalfinancemanager.domain.counterparty;

import com.keanusantos.personalfinancemanager.domain.counterparty.dto.mapper.CounterPartyResponseDTOMapper;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.CreateCounterPartyDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.PutCounterPartyDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.response.CounterPartyResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CounterPartyService {

    @Autowired
    private CounterPartyRepository repository;

    @Autowired
    private UserService userService;

    public CounterParty findByIdEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    };

    public List<CounterPartyResponseDTO> findAll() {
        return repository.findAll().stream().map(CounterPartyResponseDTOMapper::toResponse).toList();
    }

    public CounterPartyResponseDTO findById(Long id) {
        return CounterPartyResponseDTOMapper.toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id)));
    }

    public CounterPartyResponseDTO insert(CreateCounterPartyDTO obj) {
        User user = userService.findByIdEntity(obj.user());
        CounterParty counterparty = CounterPartyResponseDTOMapper.toEntity(obj, user);

        if (repository.existsByName(obj.name())) {
            throw new ResourceAlreadyExistsException("Name " + obj.name() + " already exists");
        }
        if (repository.existsByTaxId(obj.taxId())) {
            throw new ResourceAlreadyExistsException("TaxId " + obj.taxId() + " already exists");
        }

        repository.save(counterparty);
        return CounterPartyResponseDTOMapper.toResponse(counterparty);
    }

    public CounterPartyResponseDTO update(Long id, PutCounterPartyDTO newData) {
        CounterParty counterParty = findByIdEntity(id);
        updateData(counterParty, newData);
        repository.save(counterParty);
        return CounterPartyResponseDTOMapper.toResponse(counterParty);
    }

    public void updateData(CounterParty counterParty, PutCounterPartyDTO newData) {

        if (repository.existsByNameAndIdNot(newData.name(), counterParty.getId())) {
            throw new ResourceAlreadyExistsException("Name " + newData.name() + " already exists");
        }

        if (repository.existsByTaxIdAndIdNot(newData.taxId(), counterParty.getId())) {
            throw new ResourceAlreadyExistsException("TaxId " + newData.taxId() + " already exists");
        }

        counterParty.setName(newData.name());
        counterParty.setTaxId(newData.taxId());
        User user = userService.findByIdEntity(newData.user());
        counterParty.setUser(user);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }

        repository.deleteById(id);
    }
}
