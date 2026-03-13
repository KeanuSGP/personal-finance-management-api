package com.keanusantos.personalfinancemanager.domain.transaction;

import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.response.FinancialAccountResponseDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PutInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.installment.update.PatchInstallmentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.CreateTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.PutTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.request.transaction.PatchTransactionDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.response.TransactionResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

    @Autowired
    private TransactionService service;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<TransactionResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value="/me")
    public List<TransactionResponseDTO> findAllByAuthenticatedUser() {
        return service.findAllByAuthenticatedUser();
    }

    @GetMapping(value = "/{id}")
    public TransactionResponseDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping(value = "/account/{name}")
    public List<TransactionResponseDTO> findByFinancialAccountName(@PathVariable String name) {
        return service.findByFinancialAccountName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDTO insert(@Valid @RequestBody CreateTransactionDTO obj) {
        return service.insert(obj);
    }

    @PutMapping(value = "/{id}")
    public TransactionResponseDTO update(@Valid @PathVariable Long id, @RequestBody @Valid PutTransactionDTO obj) {
        return service.update(id, obj);
    }

    @PutMapping(value = "/{transactionId}/installment/{installmentId}")
    public PutInstallmentDTO putUpdateInstallment(@PathVariable Long transactionId, @PathVariable Long installmentId, @Valid @RequestBody PutInstallmentDTO obj) {
        return service.putUpdateInstallment(transactionId, installmentId, obj);
    }

    @PatchMapping(value = "/{id}")
    public TransactionResponseDTO partialUpdateTransaction(@PathVariable Long id, @RequestBody PatchTransactionDTO dto) {
        return service.partialUpdateTransaction(id, dto);
    }

    @PatchMapping(value = "/{transactionId}/installment/{installmentId}")
    public PatchInstallmentDTO partialUpdateInstallment(@PathVariable Long transactionId, @PathVariable Long installmentId, @Valid @RequestBody PatchInstallmentDTO obj) {
        return service.partialUpdateInstallment(transactionId, installmentId, obj);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @DeleteMapping(value = "/{transactionId}/installment/{installmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInstallment(@PathVariable Long transactionId, @PathVariable Long installmentId) {
        service.deleteInstallment(transactionId, installmentId);
    }
}
