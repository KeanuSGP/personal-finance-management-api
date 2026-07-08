package com.keanusantos.personalfinancemanager.domain.dashboard;

import com.keanusantos.personalfinancemanager.domain.auth.AuthService;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountRepository;
import com.keanusantos.personalfinancemanager.domain.payment.Payment;
import com.keanusantos.personalfinancemanager.domain.payment.PaymentRepository;
import com.keanusantos.personalfinancemanager.domain.payment.dto.mapper.PaymentDTOMapper;
import com.keanusantos.personalfinancemanager.domain.payment.dto.response.ResponsePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.TransactionRepository;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.mapper.TransactionDTOMapper;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.InstallmentRepository;
import com.keanusantos.personalfinancemanager.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private FinancialAccountRepository financialAccountRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private InstallmentRepository installmentRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private TransactionRepository transactionRepository;

    public DashboardDTO dashboard() {
        User user = authService.getAuthenticatedUser();
        Float balance = financialAccountRepository.sumBalanceByUserId(user.getId());
        Float totalBalance =  balance == null ? 0 :  balance;
        List<Payment> latest = paymentRepository.findTop3ByUserIdOrderByMomentDesc(user.getId());
        List<Transaction> latestTransactions = transactionRepository.findTop3ByUserIdAndInstallmentsStatusOrderByIdDesc(user.getId(), InstallmentStatus.PENDING);
        LocalDate now = LocalDate.now();
        Instant begin = now
                        .withDayOfMonth(1)
                                .atStartOfDay()
                                        .toInstant(ZoneOffset.UTC);

        LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());
        ZonedDateTime lastMomentOfMonth = lastDay.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());
        Instant end = lastMomentOfMonth.toInstant();
        Double monthlyRevenue = installmentRepository.sumRevenueByActualMonthAndUserId(user.getId(), begin, end);
        Double monthlyExpense = installmentRepository.sumExpenseByActualMonthAndUserId(user.getId(), begin, end);
        return new DashboardDTO(totalBalance, latest.stream().map(PaymentDTOMapper::toResponse).toList(), latestTransactions.stream().map(TransactionDTOMapper::toResponse).toList(),monthlyRevenue, monthlyExpense);
    }
}
