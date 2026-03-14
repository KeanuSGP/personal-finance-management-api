package com.keanusantos.personalfinancemanager.domain.financialaccount;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.mapper.FinancialAccountDTOMapper;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.CreateAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.request.PutAccountDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.response.FinancialAccountResponseDTO;
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
import java.util.stream.Collectors;

@Service
public class FinancialAccountService {

    @Autowired
    private FinancialAccountRepository repository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Transactional
    public FinancialAccount findByIdEntity(Long id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    public List<FinancialAccountResponseDTO> findAll() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {
            throw new BusinessException("Not authenticated user found",  HttpStatus.UNAUTHORIZED);
        }
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleName.ROLE_ADMIN));
        if (!isAdmin) {throw new BusinessException("Access denied",  HttpStatus.FORBIDDEN);}

        return repository.findAll().stream().map(FinancialAccountDTOMapper::toFinancialAccountResponseDTO).toList();
    }

    @Transactional
    public List<FinancialAccountResponseDTO> findAllByUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }

        return repository.findAllByUserId(user.getId()).stream().map(FinancialAccountDTOMapper::toFinancialAccountResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public FinancialAccountResponseDTO findById(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        FinancialAccount finAcc = findByIdAndUserId(id, user.getId());

        return FinancialAccountDTOMapper.toFinancialAccountResponseDTO(finAcc);
    }

    @Transactional
    public FinancialAccountResponseDTO insert(CreateAccountDTO account) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (repository.existsByNameAndUserId(account.name(), user.getId())) {
            throw new ResourceAlreadyExistsException("This account already exists: " + account.name());
        }
        FinancialAccount financialAccount = FinancialAccountDTOMapper.toEntity(account, user);
        repository.save(financialAccount);
        return FinancialAccountDTOMapper.toFinancialAccountResponseDTO(financialAccount);

    }

    @Transactional
    public FinancialAccountResponseDTO update(Long id, PutAccountDTO newAccountData) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }

        FinancialAccount acc = findByIdAndUserId(id, user.getId());
        updateAccount(acc, newAccountData);
        repository.save(acc);
        return  FinancialAccountDTOMapper.toFinancialAccountResponseDTO(acc);

    }

    public void updateAccount(FinancialAccount acc, PutAccountDTO newData) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (repository.existsByNameAndUserIdAndIdNot(newData.name(), user.getId(),  acc.getId())) {
            throw new ResourceAlreadyExistsException("Name not available: " + newData.name());
        }

        acc.setName(newData.name());
        acc.setBalance(newData.balance());
    }

    @Transactional
    public void delete(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }

        findByIdAndUserId(id, user.getId());

        if (transactionRepository.existsByFinancialAccountIdAndUser_Id(id, user.getId())) {
            throw new BusinessException("This financial account has associated transactions", HttpStatus.BAD_REQUEST);
        }

        repository.deleteById(id);
    }

    @Transactional
    public FinancialAccount findByIdAndUserId(Long id, Long userId) {
        return repository.findByIdAndUserId(id, userId).orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    public FinancialAccount findByNameAndUserId(String name, Long userId) {
        return repository.findByNameAndUserId(name, userId).orElseThrow(ResourceNotFoundException::new);
    }
}
