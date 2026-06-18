package com.neobank.service;

import com.neobank.entity.*;
import com.neobank.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)   // ✅ FIX
class LoanRepaymentServiceTest {

    @Mock
    private LoanRepaymentRepository repo;

    @Mock
    private LoanAccountRepository loanAccountRepo;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private LoanRepaymentService service;

    @Test
    void testPayAlreadyPaid() {

        // ✅ Create User
        User user = new User();
        user.setId(1L);

        // ✅ Create LoanAccount
        LoanAccount acc = new LoanAccount();
        acc.setUser(user);

        // ✅ Create Repayment
        LoanRepayment r = new LoanRepayment();
        r.setPaymentStatus(LoanRepayment.Status.PAID);
        r.setLoanAccount(acc);

        // ✅ Mock repository
        when(repo.findById(1L)).thenReturn(Optional.of(r));

        // ✅ Mock current user
        when(currentUserService.getCurrentUser()).thenReturn(user);

        // ✅ Test
        assertThrows(ResponseStatusException.class,
                () -> service.pay(1L));
    }
}
