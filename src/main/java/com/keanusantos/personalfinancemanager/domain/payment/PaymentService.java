package com.keanusantos.personalfinancemanager.domain.payment;

import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountService;
import com.keanusantos.personalfinancemanager.domain.payment.dto.mapper.PaymentDTOMapper;
import com.keanusantos.personalfinancemanager.domain.payment.dto.request.CreatePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.payment.dto.response.ResponsePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.InstallmentRepository;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private FinancialAccountService financialAccountService;
    @Autowired
    private InstallmentRepository installmentRepository;

    public ResponsePaymentDTO findById(Long id) {
        return PaymentDTOMapper.toResponse(paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id)));
    }

    public List<ResponsePaymentDTO> findAll() {
        return paymentRepository.findAll().stream().map(PaymentDTOMapper::toResponse).toList();
    }

    public ResponsePaymentDTO insert(Long iId, CreatePaymentDTO dto) {
        FinancialAccount account = financialAccountService.findByIdEntity(dto.financialAccount());
        Installment installment = installmentRepository.findById(iId).orElseThrow(() -> new ResourceNotFoundException(iId));
        Payment payment = installment.pay(account);
        installmentRepository.save(installment);
        paymentRepository.save(payment);
        return PaymentDTOMapper.toResponse(payment);
    }

    public void delete(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() ->  new ResourceNotFoundException(id));
        Installment installment =  installmentRepository.findById(payment.getInstallment().getId()).orElseThrow(() ->  new ResourceNotFoundException(payment.getInstallment().getId()));

        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException(id);
        }

        installment.removePayment(payment);
        installmentRepository.save(installment);
        paymentRepository.deleteById(id);
    }
}
