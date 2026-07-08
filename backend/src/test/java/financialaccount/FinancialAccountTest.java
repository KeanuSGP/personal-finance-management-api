package financialaccount;

import com.keanusantos.personalfinancemanager.domain.financialaccount.FinancialAccount;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FinancialAccountTest {

    FinancialAccount finAcc;

    @BeforeEach
    void setup() {
        finAcc = new FinancialAccount(null, "Account", 5000f, null);
    }

    @Test
    void shouldIncreaseBalance() {
        finAcc.credit(100f);
        assertEquals(5100, finAcc.getBalance());
    }

    @Test
    void shouldDecreaseBalance() {
        finAcc.debit(100f);
        assertEquals(4900, finAcc.getBalance());
    }

    @Test
    void shouldThrowBusinessException() {
        assertThrows(BusinessException.class,  () -> finAcc.debit(6000f));
    }
}
