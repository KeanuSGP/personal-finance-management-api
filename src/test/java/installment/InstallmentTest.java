package installment;

import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.domain.payment.Payment;
import com.keanusantos.personalfinancemanager.domain.transaction.Transaction;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.InstallmentStatus;
import com.keanusantos.personalfinancemanager.domain.transaction.enums.TransactionType;
import com.keanusantos.personalfinancemanager.domain.transaction.installment.Installment;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InstallmentTest {

    private FinancialAccount finAcc;
    private Transaction t;
    String tDescription;

    @BeforeEach
    void setup() {
        finAcc = new FinancialAccount(null, "Conta 1", 2000f, null);
        tDescription = "transaction for the payment test of installment entity";
    }

    @Test
    void shouldUpdateBalanceWhenPaymentTypeIsDebit() {
        t = new Transaction(null, "NF222222", LocalDate.now(), TransactionType.DEBIT,  tDescription, new HashSet<>(), new ArrayList<>(), null, null, null);
        Installment installment = new Installment(null, 1, 300f, LocalDate.now(), InstallmentStatus.PENDING, t);

        installment.pay(finAcc);

        assertEquals(InstallmentStatus.PAID, installment.getStatus());
        assertEquals(1700f, finAcc.getBalance());
    }

    @Test
    void shouldUpdateBalanceWhenPaymentTypeIsCredit() {
        t = new Transaction(null, "NF222222", LocalDate.now(), TransactionType.CREDIT,  tDescription, new HashSet<>(), new ArrayList<>(), null, null, null);
        Installment installment = new Installment(null, 1, 300f, LocalDate.now(), InstallmentStatus.PENDING, t);

        installment.pay(finAcc);

        assertEquals(InstallmentStatus.PAID, installment.getStatus());
        assertEquals(2300f, finAcc.getBalance());
    }

    @Test
    void shouldIncreaseBalanceWhenPaymentTypeIsDebit() {
        t = new Transaction(null, "NF222222", LocalDate.now(), TransactionType.DEBIT,  tDescription, new HashSet<>(), new ArrayList<>(), null, null, null);
        Installment installment = new Installment(null, 1, 300f, LocalDate.now(), InstallmentStatus.PAID, t);

        Payment payment = new Payment(null, Instant.now(), finAcc, installment);

        installment.removePayment(payment);

        assertEquals(InstallmentStatus.PENDING, installment.getStatus());
        assertEquals(2300f, finAcc.getBalance());
    }

    @Test
    void shouldDecreaseBalanceWhenPaymentTypeIsCredit() {
        t = new Transaction(null, "NF222222", LocalDate.now(), TransactionType.CREDIT,  tDescription, new HashSet<>(), new ArrayList<>(), null, null, null);
        Installment installment = new Installment(null, 1, 300f, LocalDate.now(), InstallmentStatus.PAID, t);

        Payment payment = new Payment(null, Instant.now(), finAcc, installment);

        installment.removePayment(payment);

        assertEquals(InstallmentStatus.PENDING, installment.getStatus());
        assertEquals(1700f, finAcc.getBalance());
    }

    @Test
    void shouldThrowsBusinessException() {
        t = new Transaction(null, "NF222222", LocalDate.now(), TransactionType.CREDIT,  tDescription, new HashSet<>(), new ArrayList<>(), null, null, null);
        Installment installment = new Installment(null, 1, 2300f, LocalDate.now(), InstallmentStatus.PAID, t);

        Payment payment = new Payment(null, Instant.now(), finAcc, installment);

        assertThrows(BusinessException.class, () -> installment.pay(finAcc));
    }

}
