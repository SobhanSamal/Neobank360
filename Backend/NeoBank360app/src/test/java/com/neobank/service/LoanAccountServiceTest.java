package com.neobank.service;

import com.neobank.entity.*;
import com.neobank.repository.LoanAccountRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoanAccountServiceTest {

    @Mock
    private LoanAccountRepository repo;

    @InjectMocks
    private LoanAccountService service;

    @Test
    void testCreateAccount() {

        LoanProduct product = new LoanProduct();
        product.setAnnualInterestRate(10.0);

        User user = new User();

        LoanApplication app = new LoanApplication();
        app.setRequestedAmount(100000.0);
        app.setRequestedTenureMonths(12);
        app.setLoanProduct(product);
        app.setUser(user);

        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        LoanAccount acc = service.createAccount(app);

        assertNotNull(acc);
        assertEquals(100000.0, acc.getPrincipalAmount());
        assertEquals(12, acc.getTenureMonths());
    }
}