package com.keanusantos.personalfinancemanager.domain.transaction;

import com.keanusantos.personalfinancemanager.domain.counterparty.CounterParty;
import com.keanusantos.personalfinancemanager.domain.counterparty.CounterPartyService;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountService;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.mapper.InstallmentDTOMapper;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.mapper.TransactionDTOMapper;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.PutInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PatchInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.CreateTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.create.CreateInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.PutTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.PatchTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.response.TransactionResponseDTO;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.EmptyArgumentException;
import com.keanusantos.personalfinancemanager.exception.ResourceAlreadyExistsException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
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
        CounterParty counterParty = counterPService.findById(obj.counterParty());
        FinancialAccount financialAccount = finAccService.findById(obj.financialAccount());

        if (repository.existsByDoc(obj.doc())) {
            throw new ResourceAlreadyExistsException("A transaction already exists with this document");
        };

        Transaction transaction = TransactionDTOMapper.toTransaction(obj, counterParty, financialAccount);

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
            PatchInstallmentDTO completeDTO = new PatchInstallmentDTO(
                    null,
                    installmentNumber,
                    dto.amount(),
                    dto.dueDate(),
                    dto.status()
            );
            Installment installment = InstallmentDTOMapper.updateDTOtoInstallment(completeDTO, transaction);
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

    public PutInstallmentDTO putUpdateInstallment(Long transactionId, Long id, PutInstallmentDTO installmentDTO) {
        Transaction t = repository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException(transactionId));
        Installment i = t.getInstallments().stream().filter(c -> c.getId() == id).findFirst().orElseThrow(() -> new ResourceNotFoundException(id));

        if (installmentDTO.installmentNumber() < 0) {
            throw new BusinessException("The number of the installments cannot be less than 1", HttpStatus.BAD_REQUEST);
        }

        if (installmentDTO.amount() == null) {
            throw new EmptyArgumentException("amount");
        }
        if (installmentDTO.dueDate() == null) {
            throw new EmptyArgumentException("dueDate");
        }
        if (installmentDTO.status() == null) {
            throw new EmptyArgumentException("status");
        }

        i.putUpdateData(installmentDTO);
        repository.save(t);
        return InstallmentDTOMapper.toPutInstallmentDTO(i);
    }

    private void putUpdateTransaction(Transaction transaction, PutTransactionDTO newData) {
        if (repository.existsByDocAndIdNot(newData.doc(), transaction.getId())) {
            throw new ResourceAlreadyExistsException("The transaction already exists in the system");
        }
        transaction.setDoc(newData.doc());
        transaction.setIssueDate(newData.issueDate());
        transaction.setType(newData.type());
        transaction.setDescription(newData.description());
        transaction.setCounterparty(counterPService.findById(newData.counterParty()));
        transaction.setFinancialAccount(finAccService.findById(newData.financialAccount()));

    }


    public TransactionResponseDTO partialUpdateTransaction(Long id, PatchTransactionDTO newData) {

        Transaction transaction = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));

        if (repository.existsByDocAndIdNot(newData.doc(), transaction.getId())) {
            throw new ResourceAlreadyExistsException("The transaction already exists in the system");
        }

        if (newData.doc() != null) {
            transaction.setDoc(newData.doc());
        }

        if (newData.issueDate() != null) {
            transaction.setIssueDate(newData.issueDate());
        }

        if (newData.type() != null) {
            transaction.setType(newData.type());
        }

        if (newData.description() != null) {
            transaction.setDescription(newData.description());
        }

        if (newData.counterParty() != null) {
            CounterParty counterP = counterPService.findById(newData.counterParty());
            transaction.setCounterparty(counterP);
        }

        if (newData.financialAccount() != null) {
            FinancialAccount finAcc = finAccService.findById(newData.financialAccount());
            transaction.setFinancialAccount(finAcc);
        }

        repository.save(transaction);
        return TransactionDTOMapper.toResponse(transaction);

    }

    public PatchInstallmentDTO parcialUpdateInstallment(Long transactionId, Long installmentId, PatchInstallmentDTO newData) {
        Transaction transaction = repository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException(transactionId));
        Installment installment = transaction.getInstallments().stream().filter(a -> a.getId().equals(installmentId)).findFirst().orElseThrow(() -> new ResourceNotFoundException(installmentId));

        transaction.getInstallments().forEach(a -> {
            if (a.getInstallmentNumber() == installment.getInstallmentNumber() && a.getId() != installmentId) {
                throw new ResourceAlreadyExistsException("An installment of this number already exists: " + a.getInstallmentNumber());
            }
        });

        installment.parcialUpdateData(newData);
        repository.save(transaction);

        return InstallmentDTOMapper.toUpdateInstallmentDTO(installment);

    }

    public void delete(Long id) {
        if (repository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public void deleteInstallment(Long tId, Long iId) {
        Transaction t = repository.findById(tId).orElseThrow(() -> new ResourceNotFoundException(tId));
        Installment i = t.getInstallments().stream().filter(obj -> obj.getId() == iId).findFirst().orElseThrow(() -> new ResourceNotFoundException(iId));
        t.getInstallments().remove(i);
        repository.save(t);
    }


}
