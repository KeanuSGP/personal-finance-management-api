package com.keanusantos.personalfinancemanager.domain.dashboard;

import com.keanusantos.personalfinancemanager.domain.auth.AuthService;
import com.keanusantos.personalfinancemanager.domain.category.CategoryRepository;
import com.keanusantos.personalfinancemanager.domain.dashboard.summary.DashboardCategorySummary;
import com.keanusantos.personalfinancemanager.domain.dashboard.summary.MensalSummaryDTO;
import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccountRepository;
import com.keanusantos.personalfinancemanager.domain.payment.Payment;
import com.keanusantos.personalfinancemanager.domain.payment.PaymentRepository;
import com.keanusantos.personalfinancemanager.domain.payment.dto.mapper.PaymentDTOMapper;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.TransactionRepository;
import com.keanusantos.personalfinancemanager.domain.transaction.dto.mapper.TransactionDTOMapper;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.InstallmentRepository;
import com.keanusantos.personalfinancemanager.domain.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private CategoryRepository categoryRepository;

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
        Double sixMonthsRevenue = installmentRepository.sumLastSixMonthRevenueByUserId(user.getId());
        Double sixMonthExpense = installmentRepository.sumLastSixMonthExpenseByUserId(user.getId());
        List<DashboardCategorySummary> revenueC = categoryRepository.sumTop5RevenueCategoryByUserId(user.getId());
        List<DashboardCategorySummary> expenseC = categoryRepository.sumTop5ExpenseCategoryByUserId(user.getId());
        return new DashboardDTO(totalBalance, latest.stream().map(PaymentDTOMapper::toResponse).toList(),
                latestTransactions.stream().map(TransactionDTOMapper::toResponse).toList(),
                monthlyRevenue,
                monthlyExpense,
                sixMonthsRevenue,
                sixMonthExpense,
                revenueC,
                expenseC,
                getLastSixMonthsExpenseAndRevenue());
    }

    private List<MensalSummaryDTO> getLastSixMonthsExpenseAndRevenue() {
        User user = authService.getAuthenticatedUser();
        List<Installment> t = installmentRepository.last6MonthsInstallments(user.getId());
        t.sort(Comparator.comparing(i -> i.getPayment().getMoment()));
        Map<String, MensalSummaryDTO> lastSixMonthsMap = t.stream().collect(Collectors.toMap(i -> getMonth(i.getPayment().getMoment()),
                i -> new MensalSummaryDTO(getMonth(i.getPayment().getMoment()),i.getTransaction().getType() == TransactionType.CREDIT ? i.getAmount() : 0.0,
                        i.getTransaction().getType() == TransactionType.DEBIT? i.getAmount() : 0.0),
                (existing, actual) -> new MensalSummaryDTO(existing.month(),existing.revenue() + actual.revenue(),existing.expense() + actual.expense()
                ), LinkedHashMap::new));
        List<MensalSummaryDTO> lastSixMonths = lastSixMonthsMap.entrySet().stream().map(entry -> new MensalSummaryDTO(entry.getKey(), entry.getValue().revenue(), entry.getValue().expense())).collect(Collectors.toList());
        return lastSixMonths;
    }

    private String getMonth(Instant date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.US);
        return formatter.format(date.atZone(ZoneId.of("UTC")));
    }
}
