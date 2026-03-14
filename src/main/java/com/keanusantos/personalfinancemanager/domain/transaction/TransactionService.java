package com.keanusantos.personalfinancemanager.domain.transaction;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.category.CategoryService;
import com.keanusantos.personalfinancemanager.domain.counterparty.Counterparty;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterpartyService;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountService;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.mapper.InstallmentDTOMapper;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.mapper.TransactionDTOMapper;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PutInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PatchInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.CreateTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.create.CreateInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.PutTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.PatchTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.response.TransactionResponseDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;
    @Autowired
    private CounterpartyService counterPService;
    @Autowired
    private FinancialAccountService finAccService;
    @Autowired
    private CategoryService categoryService;

    @Transactional
    public Transaction findByIdEntity(Long id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @Transactional
    public List<TransactionResponseDTO> findAll() {
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User entity = user.getUser();
        if (entity == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        boolean isAdmin = entity.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleName.ROLE_ADMIN));
        if (!isAdmin) {
            throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);
        }
        return repository.findAll().stream().map(TransactionDTOMapper::toResponse).toList();
    }

    @Transactional
    public List<TransactionResponseDTO> findAllByAuthenticatedUser(){
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);}

        List<Transaction> transactions = repository.findAllByUser_Id(user.getId());
        return transactions.stream().map(TransactionDTOMapper::toResponse).toList();
    }

    @Transactional
    public TransactionResponseDTO findById(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        Transaction transaction = repository.findByIdAndUser_Id(id, user.getId()).orElseThrow(ResourceNotFoundException::new);

        return TransactionDTOMapper.toResponse(transaction);
    }

    @Transactional
    public List<TransactionResponseDTO> findByFinancialAccountName(String name) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }

        FinancialAccount fin = finAccService.findByNameAndUserId(name, user.getId());

        List<Transaction> transactions = repository.findAllByFinancialAccountIdAndUserId(fin.getId(), user.getId());

        if(transactions.isEmpty()) {
            throw new ResourceNotFoundException();
        }

        return transactions.stream().map(TransactionDTOMapper::toResponse).toList();
    }

    @Transactional
    public TransactionResponseDTO insert(CreateTransactionDTO obj) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }

        Counterparty counterparty = counterPService.findByIdAndUserId(obj.counterparty(), user.getId());
        FinancialAccount financialAccount = finAccService.findByIdAndUserId(obj.financialAccount(), user.getId());
        Set<Category> categories = categoryService.findCategoriesByOwner(obj.categories(), user.getId());


        if (repository.existsByDocAndUser_Id(obj.doc(), user.getId())) {
            throw new ResourceAlreadyExistsException("Document already exists: " +  obj.doc());
        };

        Transaction transaction = TransactionDTOMapper.toTransaction(obj, categories, counterparty, financialAccount, user);

        transaction.addListOfInstallments(createInstallments(obj.installments(), transaction));

        repository.save(transaction);

        return TransactionDTOMapper.toResponse(transaction);
    }

    // cria uma lista de installments a partir de installmentDTO e retorna essa lista
    public List<Installment> createInstallments(List<CreateInstallmentDTO> list, Transaction transaction) {
        List<Installment> installments = new ArrayList<>();

        if (list.isEmpty()) {
            throw new BusinessException("The transaction requires at least one installment", HttpStatus.BAD_REQUEST);
        }

        if (list.size() > 96) {
            throw new BusinessException("The installments size must not be greater 96", HttpStatus.BAD_REQUEST);
        }

        list.sort(Comparator.comparing(CreateInstallmentDTO::dueDate));

        int installmentNumber = 0;

        for (CreateInstallmentDTO dto: list) {
            installmentNumber++;
            Installment installment = new Installment();
            installment.setAmount(dto.amount());
            installment.setInstallmentNumber(installmentNumber);
            installment.setDueDate(dto.dueDate());
            installment.setStatus(InstallmentStatus.PENDING);
            installment.setTransaction(transaction);
            installments.add(installment);
        }

        return installments;
    }

    @Transactional
    public TransactionResponseDTO update(Long id, PutTransactionDTO newData) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.FORBIDDEN);
        }
        Transaction transaction = repository.findByIdAndUser_Id(id, user.getId()).orElseThrow(ResourceNotFoundException::new);
        putUpdateTransaction(transaction, user, newData);
        repository.save(transaction);

        return TransactionDTOMapper.toResponse(transaction);
    }

    @Transactional
    private void putUpdateTransaction(Transaction transaction, User user, PutTransactionDTO newData) {
        if (repository.existsByDocAndUser_IdAndIdNot(newData.doc(), user.getId(), transaction.getId())) {
            throw new ResourceAlreadyExistsException("The transaction already exists in the system");
        }

        Counterparty counterparty = counterPService.findByIdAndUserId(newData.counterparty(), user.getId());
        FinancialAccount financialAccount = finAccService.findByIdAndUserId(newData.financialAccount(), user.getId());
        Set<Category> categories = categoryService.findCategoriesByOwner(newData.categories(), user.getId());

        transaction.setDoc(newData.doc());
        transaction.setIssueDate(newData.issueDate());
        transaction.setDescription(newData.description());
        transaction.setCategories(categories);
        transaction.setCounterparty(counterparty);
        transaction.setFinancialAccount(financialAccount);
    }

    @Transactional
    public PutInstallmentDTO putUpdateInstallment(Long transactionId, Long id, PutInstallmentDTO installmentDTO) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        Transaction t = repository.findByIdAndUser_Id(transactionId, user.getId()).orElseThrow(ResourceNotFoundException::new);

        Installment i = t.getInstallments().stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(ResourceNotFoundException::new);

        if (i.getStatus() == InstallmentStatus.PAID) {
            throw new BusinessException("You cannot change a paid installment", HttpStatus.BAD_REQUEST);
        }

        boolean existsByInstallmentNumber = t.getInstallments().stream().anyMatch(c -> !c.getId().equals(id) && c.getInstallmentNumber().equals(installmentDTO.installmentNumber()));

        if (existsByInstallmentNumber) {
            throw new BusinessException("This installment number already exists: " + installmentDTO.installmentNumber(), HttpStatus.BAD_REQUEST);
        }

        i.putUpdateData(installmentDTO);
        repository.save(t);
        return InstallmentDTOMapper.toPutInstallmentDTO(i);
    }

    @Transactional
    public TransactionResponseDTO partialUpdateTransaction(Long id, PatchTransactionDTO newData) {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        Transaction t = repository.findByIdAndUser_Id(id, user.getId()).orElseThrow(ResourceNotFoundException::new);

        if (newData.categories() != null) {
            Set<Category> categories = newData.categories().stream().map(cId -> categoryService.findByIdEntity(cId)).collect(Collectors.toSet());
            if (!categories.isEmpty()) {
                t.setCategories(categories);
            }
        }

        Counterparty counterP = newData.counterparty() != null ? counterPService.findByIdAndUserId(newData.counterparty(), user.getId()): t.getCounterparty();
        FinancialAccount finAcc = newData.financialAccount() != null ? finAccService.findByIdAndUserId(newData.financialAccount(), user.getId()): t.getFinancialAccount();


        if (repository.existsByDocAndUser_IdAndIdNot(newData.doc(), user.getId(), t.getId())) {
            throw new ResourceAlreadyExistsException("The transaction already exists in the system");
        }

        t.partialUpdateTransaction(newData, counterP, finAcc, user);
        repository.save(t);
        return TransactionDTOMapper.toResponse(t);

    }

    @Transactional
    public PatchInstallmentDTO partialUpdateInstallment(Long transactionId, Long installmentId, PatchInstallmentDTO newData) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }

        Transaction transaction = repository.findByIdAndUser_Id(transactionId, user.getId()).orElseThrow(ResourceNotFoundException::new);
        Installment installment = transaction.getInstallments().stream().filter(a -> a.getId().equals(installmentId)).findFirst().orElseThrow(ResourceNotFoundException::new);
        if (installment.getStatus() == InstallmentStatus.PAID) {
            throw new BusinessException("You cannot change a paid installment", HttpStatus.BAD_REQUEST);
        }
        boolean exists = transaction.getInstallments().stream().anyMatch(a -> !a.getId().equals(installmentId) && a.getInstallmentNumber().equals(newData.installmentNumber()));
        if (exists) {
            throw new BusinessException("This installment number already exists: " + newData.installmentNumber(), HttpStatus.BAD_REQUEST);
        }
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Transaction not found or access denied", HttpStatus.FORBIDDEN);
        }
        installment.partialUpdateData(newData);
        repository.save(transaction);
        return InstallmentDTOMapper.installmentToPatchDTO(installment);
    }

    @Transactional
    public void delete(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        if (!repository.existsByIdAndUser_Id(id, user.getId())) {
            throw new ResourceNotFoundException();
        }

        Transaction t = repository.findByIdAndUser_Id(id, user.getId()).orElseThrow(ResourceNotFoundException::new);

        if (repository.existsPaidInstallmentById(id)) {
            t.getInstallments().removeIf(i -> i.getStatus() != InstallmentStatus.PAID);
            repository.save(t);
        } else {
            repository.deleteById(id);
        }

    }

    @Transactional
    public void deleteInstallment(Long tId, Long iId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();

        Transaction t = repository.findById(tId).orElseThrow(ResourceNotFoundException::new);

        if (!t.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Transaction not found or access denied", HttpStatus.FORBIDDEN);
        }

        Installment i = t.getInstallments().stream().filter(a -> a.getId().equals(iId)).findFirst().orElseThrow(ResourceNotFoundException::new);
        if (i.getStatus().equals(InstallmentStatus.PAID)) {
            throw new BusinessException("You cannot delete a paid installment", HttpStatus.BAD_REQUEST);
        }
        t.getInstallments().remove(i);
        repository.save(t);
    }


}
