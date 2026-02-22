package com.keanusantos.personalfinancemanager.domain.transaction;

import com.keanusantos.personalfinancemanager.domain.category.Category;
import com.keanusantos.personalfinancemanager.domain.category.CategoryService;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterPartyService;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountService;
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
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;
    @Autowired
    private CounterPartyService counterPService;
    @Autowired
    private FinancialAccountService finAccService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;


    public Transaction findByIdEntity(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public List<TransactionResponseDTO> findAll(){
        List<Transaction> transactions = repository.findAll();
        List<TransactionResponseDTO> responseList = transactions.stream().map(TransactionDTOMapper::toResponse).toList();
        return responseList;
    }

    public TransactionResponseDTO findById(Long id) {
        Transaction transaction = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        return TransactionDTOMapper.toResponse(transaction);
    }

    public List<TransactionResponseDTO> findByFinancialAccountId(Long id) {
        finAccService.findById(id); // para lançar exceção caso não exista a conta

        List<Transaction> toConvert = repository.findByFinancialAccountId(id);

        List<TransactionResponseDTO> response = new ArrayList<>();

        for (Transaction t : toConvert) {
            TransactionResponseDTO r = TransactionDTOMapper.toResponse(t);
            response.add(r);
        }

        return response;
    }

    public TransactionResponseDTO insert(CreateTransactionDTO obj) {
        CounterParty counterParty = counterPService.findByIdEntity(obj.counterParty());
        FinancialAccount financialAccount = finAccService.findByIdEntity(obj.financialAccount());
        Set<Category> categories = obj.categories().stream().map(n -> categoryService.findByIdEntity(n)).collect(Collectors.toSet());
        User user =  userService.findByIdEntity(obj.user());

        if (repository.existsByDoc(obj.doc())) {
            throw new ResourceAlreadyExistsException("Document already exists: " +  obj.doc());
        };

        Transaction transaction = TransactionDTOMapper.toTransaction(obj, categories, counterParty, financialAccount, user);

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

    public TransactionResponseDTO update(Long id, PutTransactionDTO newData) {
        Transaction transaction = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        putUpdateTransaction(transaction, newData);
        repository.save(transaction);

        return TransactionDTOMapper.toResponse(transaction);
    }

    private void putUpdateTransaction(Transaction transaction, PutTransactionDTO newData) {
        if (repository.existsByDocAndIdNot(newData.doc(), transaction.getId())) {
            throw new ResourceAlreadyExistsException("The transaction already exists in the system");
        }

        CounterParty counterParty = counterPService.findByIdEntity(newData.counterParty());
        FinancialAccount financialAccount = finAccService.findByIdEntity(newData.financialAccount());
        Set<Category> categories = newData.categories().stream().map(n -> categoryService.findByIdEntity(n)).collect(Collectors.toSet());
        User user =  userService.findByIdEntity(newData.user());

        transaction.setDoc(newData.doc());
        transaction.setIssueDate(newData.issueDate());
        transaction.setType(newData.type());
        transaction.setDescription(newData.description());
        transaction.setCategories(categories);
        transaction.setCounterparty(counterParty);
        transaction.setFinancialAccount(financialAccount);
        transaction.setUser(user);

    }

    public PutInstallmentDTO putUpdateInstallment(Long transactionId, Long id, PutInstallmentDTO installmentDTO) {
        Transaction t = repository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException(transactionId));
        Installment i = t.getInstallments().stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() -> new ResourceNotFoundException(id));

        boolean exists = t.getInstallments().stream().anyMatch(c -> !c.getId().equals(id) && c.getInstallmentNumber().equals(installmentDTO.installmentNumber()));

        if (exists) {
            throw new BusinessException("This installment number already exists: " + installmentDTO.installmentNumber(), HttpStatus.BAD_REQUEST);
        }

        i.putUpdateData(installmentDTO);
        repository.save(t);
        return InstallmentDTOMapper.toPutInstallmentDTO(i);
    }

    public TransactionResponseDTO partialUpdateTransaction(Long id, PatchTransactionDTO newData) {
        Transaction t = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
        Set<Category> categories = newData.categories().stream().map(n -> categoryService.findByIdEntity(n)).collect(Collectors.toSet());
        CounterParty counterP = counterPService.findByIdEntity(newData.counterParty());
        FinancialAccount finAcc = finAccService.findByIdEntity(newData.financialAccount());
        User user = userService.findByIdEntity(newData.user());


        if (repository.existsByDocAndIdNot(newData.doc(), t.getId())) {
            throw new ResourceAlreadyExistsException("The transaction already exists in the system");
        }

        t.partialUpdateTransaction(t, newData, categories, counterP, finAcc, user);
        repository.save(t);
        return TransactionDTOMapper.toResponse(t);

    }

    public PatchInstallmentDTO partialUpdateInstallment(Long transactionId, Long installmentId, PatchInstallmentDTO newData) {
        Transaction transaction = repository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException(transactionId));
        Installment installment = transaction.getInstallments().stream().filter(a -> a.getId().equals(installmentId)).findFirst().orElseThrow(() -> new ResourceNotFoundException(installmentId));

        boolean exists = transaction.getInstallments().stream().anyMatch(a -> !a.getId().equals(installmentId) && a.getInstallmentNumber().equals(newData.installmentNumber()));

        if (exists) {
            throw new BusinessException("This installment number already exists: " + newData.installmentNumber(), HttpStatus.BAD_REQUEST);
        }

        installment.partialUpdateData(newData);
        repository.save(transaction);
        return InstallmentDTOMapper.installmentToPatchDTO(installment);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public void deleteInstallment(Long tId, Long iId) {
        Transaction t = repository.findById(tId).orElseThrow(() -> new ResourceNotFoundException(tId));
        Installment i = t.getInstallments().stream().filter(a -> a.getId().equals(iId)).findFirst().orElseThrow(() -> new ResourceNotFoundException(iId));
        t.getInstallments().remove(i);
        repository.save(t);
    }


}
