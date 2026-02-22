package com.keanusantos.personalfinancemanager.domain.financialaccount;

import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.mapper.FinancialAccountDTOMapper;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.CreateAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.PutAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.response.FinancialAccountResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinancialAccountService {

    @Autowired
    private FinancialAccountRepository repository;

    @Autowired
    private UserService userService;

    public FinancialAccount findByIdEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public List<FinancialAccountResponseDTO> findAll() {
        return repository.findAll().stream().map(FinancialAccountDTOMapper::toFinancialAccountResponseDTO).collect(Collectors.toList());
    }

    public FinancialAccountResponseDTO findById(Long id) {
        return FinancialAccountDTOMapper.toFinancialAccountResponseDTO(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id)));
    }

    public FinancialAccountResponseDTO insert(CreateAccountDTO account) {
        if (repository.existsByName(account.name())) {
            throw new ResourceAlreadyExistsException("Name not available: " + account.name());
        }
        User user = userService.findByIdEntity(account.user());
        FinancialAccount financialAccount = FinancialAccountDTOMapper.toEntity(account, user);
        repository.save(financialAccount);
        return FinancialAccountDTOMapper.toFinancialAccountResponseDTO(financialAccount);

    }

    public FinancialAccountResponseDTO update(Long id, PutAccountDTO newAccountData) {
        FinancialAccount acc = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        updateAccount(acc, newAccountData);
        repository.save(acc);
        return  FinancialAccountDTOMapper.toFinancialAccountResponseDTO(acc);

    }

    public void updateAccount(FinancialAccount acc, PutAccountDTO newData) {
        User user = userService.findByIdEntity(newData.user());

        if (repository.existsByNameAndIdNot(newData.name(), acc.getId())) {
            throw new ResourceAlreadyExistsException("Name not available: " + newData.name());
        }

        acc.setName(newData.name());
        acc.setBalance(newData.balance());
        acc.setUser(user);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
