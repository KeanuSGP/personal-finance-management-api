package com.keanusantos.personalfinancemanager.domain.financialaccount;

import com.keanusantos.personalfinancemanager.domain.financialaccount.exception.FinancialAccountAlreadyExists;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

@Service
public class FinancialAccountService {

    @Autowired
    private FinancialAccountRepository repository;

    @Autowired
    private UserService userService;

    public List<FinancialAccount> findAll() {
        return repository.findAll();
    }

    public FinancialAccount findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public List<FinancialAccount> findByUserId(Long id) {
        userService.findById(id);
        return repository.findByUserId(id);
    }

    public FinancialAccount insert(FinancialAccount account) {
        if (repository.existsByName(account.getName())) {
            throw new BusinessException("An account with that name already exists", HttpStatus.CONFLICT);
        }

        User user = userService.findById(account.getUser().getId());
        account.setUser(user);
        return repository.save(account);

    }

    public FinancialAccount update(Long id, FinancialAccount newAccountData) {
        FinancialAccount acc = repository.findById(id).orElseThrow();
        updateAccount(acc, newAccountData);
        return repository.save(acc);

    }

    public void updateAccount(FinancialAccount acc, FinancialAccount newData) {
        if (newData.getName().isEmpty()) {
            throw new BusinessException("The name must be filled in", HttpStatus.BAD_REQUEST);
        }

        if (newData.getBalance() < 0) {
            throw new BusinessException("The balance must be equal to or greater than zero", HttpStatus.BAD_REQUEST);
        }

        acc.setName(newData.getName());
        acc.setBalance(newData.getBalance());
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
