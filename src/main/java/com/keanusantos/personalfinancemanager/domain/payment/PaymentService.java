package com.keanusantos.personalfinancemanager.domain.payment;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountRepository;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountService;
import com.keanusantos.personalfinancemanager.domain.financialaccount.dto.response.FinancialAccountResponseDTO;
import com.keanusantos.personalfinancemanager.domain.payment.dto.mapper.PaymentDTOMapper;
import com.keanusantos.personalfinancemanager.domain.payment.dto.request.CreatePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.payment.dto.response.ResponsePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.InstallmentRepository;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import com.keanusantos.personalfinancemanager.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private FinancialAccountService financialAccountService;

    @Autowired
    private FinancialAccountRepository financialAccountRepository;

    @Autowired
    private InstallmentRepository installmentRepository;

    public List<ResponsePaymentDTO> findAllByUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);}
        List<Payment> payments = paymentRepository.findAllByUserId(user.getId());
        if (payments.isEmpty()) {throw new ResourceNotFoundException();}
        return payments.stream().map(PaymentDTOMapper::toResponse).toList();
    }

    public ResponsePaymentDTO findById(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        return PaymentDTOMapper.toResponse(paymentRepository.findByIdAndUserId(id, user.getId()).orElseThrow(ResourceNotFoundException::new));
    }

    public List<ResponsePaymentDTO> findAll() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);}
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getRole().equals(RoleName.ROLE_ADMIN));
        if (!isAdmin) {throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);}
        List<Payment> payments = paymentRepository.findAllByUserId(user.getId());
        if (payments.isEmpty()) {throw new ResourceNotFoundException();}
        return payments.stream().map(PaymentDTOMapper::toResponse).toList();
    }

    public ResponsePaymentDTO insert(Long iId, CreatePaymentDTO dto) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);}
        FinancialAccount account = financialAccountService.findByIdAndUserId(dto.financialAccount(), user.getId());
        Installment installment = installmentRepository.findByIdAndTransactionUserId(iId, user.getId()).orElseThrow(ResourceNotFoundException::new);
        boolean isOwner = installment.getTransaction().getUser().getId().equals(user.getId());
        if (!isOwner) {throw new BusinessException("Access denied", HttpStatus.FORBIDDEN);}
        Payment payment = installment.pay(account, user);
        paymentRepository.save(payment);
        installmentRepository.save(installment);
        financialAccountRepository.save(account);
        return PaymentDTOMapper.toResponse(payment);
    }

    public void delete(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userDetails.getUser();
        if (user == null) {throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);}
        Payment payment = paymentRepository.findByIdAndUserId(id, user.getId()).orElseThrow(ResourceNotFoundException::new);
        Long iId = payment.getInstallment().getId();
        Installment installment =  installmentRepository.findByIdAndTransactionUserId(iId, user.getId()).orElseThrow(ResourceNotFoundException::new);
        installment.removePayment(payment);
        installmentRepository.save(installment);
        financialAccountRepository.save(payment.getFinancialAccount());
        paymentRepository.deleteById(id);
    }
}
