package com.neobank.service;

import com.neobank.dto.*;
import com.neobank.entity.*;
import com.neobank.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock private LoanApplicationRepository repo;
    @Mock private LoanProductRepository productRepo;
    @Mock private UserRepository userRepo;
    @Mock private LoanAccountService loanAccountService;
    @Mock private RepaymentScheduleService repaymentScheduleService;

    @InjectMocks private LoanApplicationService service;

    @Test
    void testInvalidAmount() {

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("test@email.com", null)
        );

        LoanApplicationRequestDTO dto = new LoanApplicationRequestDTO();
        dto.productId = 1L;
        dto.requestedAmount = 100.0;
        dto.requestedTenureMonths = 12;

        LoanProduct product = new LoanProduct();
        product.setMinAmount(500.0);
        product.setMaxAmount(5000.0);
        product.setAllowedTenures("12,24");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");

        // ✅ FIXED
        lenient().when(productRepo.findById(1L))
                .thenReturn(Optional.of(product));

        when(userRepo.findByEmail(anyString()))
                .thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class,
                () -> service.apply(dto));
    }
}