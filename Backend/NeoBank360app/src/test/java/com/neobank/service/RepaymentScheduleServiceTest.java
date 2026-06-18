package com.neobank.service;

import com.neobank.entity.LoanAccount;
import com.neobank.repository.LoanRepaymentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)   // ✅ FIX
class RepaymentScheduleServiceTest {

    @Mock
    private LoanRepaymentRepository repo;

    @InjectMocks
    private RepaymentScheduleService service;

    @Test
    void testGenerateSchedule() {

        LoanAccount acc = new LoanAccount();
        acc.setPrincipalAmount(100000.0);
        acc.setAnnualInterestRate(10.0);
        acc.setEmiAmount(8791.0);
        acc.setTenureMonths(12);

        // ✅ Optional but safe
        when(repo.save(any())).thenReturn(null);

        service.generateSchedule(acc);

        verify(repo, atLeast(12)).save(any());
    }
}