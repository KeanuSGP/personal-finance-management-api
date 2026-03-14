package com.keanusantos.personalfinancemanager.domain.counterparty;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.mapper.CounterPartyDTOMapper;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.CreateCounterPartyDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.request.PutCounterpartyDTO;
import com.keanusantos.personalfinancemanager.domain.counterparty.dto.response.CounterPartyResponseDTO;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.transaction.TransactionRepository;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CounterpartyService {

    @Autowired
    private CounterpartyRepository repository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    public Counterparty findByIdEntity(Long id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    public List<CounterPartyResponseDTO> findAll() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }
        boolean admin = user.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleName.ROLE_ADMIN));
        if (!admin) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
        return repository.findAll().stream().map(CounterPartyDTOMapper::toResponse).toList();
    }

    @Transactional
    public List<CounterPartyResponseDTO> findAllByUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }

        return repository.findAllByUserId(user.getId()).stream().map(CounterPartyDTOMapper::toResponse).toList();
    }

    @Transactional
    public CounterPartyResponseDTO findById(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }

        Counterparty counterP = findByIdAndUserId(id, user.getId());

        return CounterPartyDTOMapper.toResponse(counterP);
    }

    @Transactional
    public CounterPartyResponseDTO insert(CreateCounterPartyDTO obj) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }

        if (repository.existsByNameAndUserId(obj.name(), user.getId())) {
            throw new ResourceAlreadyExistsException("Name " + obj.name() + " already exists");
        }
        if (repository.existsByTaxIdAndUserId(obj.taxId(),  user.getId())) {
            throw new ResourceAlreadyExistsException("TaxId " + obj.taxId() + " already exists");
        }

        Counterparty counterparty = CounterPartyDTOMapper.toEntity(obj, user);

        repository.save(counterparty);
        return CounterPartyDTOMapper.toResponse(counterparty);
    }

    @Transactional
    public CounterPartyResponseDTO update(Long id, PutCounterpartyDTO newData) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }
        Counterparty counterParty = findByIdAndUserId(id, user.getId());
        updateData(counterParty, user, newData);
        repository.save(counterParty);
        return CounterPartyDTOMapper.toResponse(counterParty);
    }

    public void updateData(Counterparty counterParty, User u, PutCounterpartyDTO newData) {

        if (repository.existsByNameAndUserIdAndIdNot(newData.name(), u.getId(), counterParty.getId())) {
            throw new ResourceAlreadyExistsException("Name " + newData.name() + " already exists");
        }

        if (repository.existsByTaxIdAndUserIdAndIdNot(newData.taxId(), u.getId(), counterParty.getId())) {
            throw new ResourceAlreadyExistsException("TaxId " + newData.taxId() + " already exists");
        }

        counterParty.setName(newData.name());
        counterParty.setTaxId(newData.taxId());
    }

    @Transactional
    public void delete(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }

        if (!repository.existsByIdAndUserId(id, user.getId())) {
            throw new ResourceNotFoundException();
        }

        if (transactionRepository.existsByCounterpartyIdAndUser_Id(id, user.getId())) {
            throw new BusinessException("This counterparty has associated transactions", HttpStatus.BAD_REQUEST);
        }


        repository.deleteById(id);
    }

    @Transactional
    public Counterparty findByIdAndUserId(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId).orElseThrow(() -> new BusinessException("Resource not found or access denied", HttpStatus.FORBIDDEN));
    }
}
