package com.neobank.service;

import com.neobank.dto.LoanProductDTO;
import com.neobank.repository.LoanProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)   // ✅ FIX
class LoanProductServiceTest {

    @Mock
    private LoanProductRepository repo;

    @InjectMocks
    private LoanProductService service;

    @Test
    void testInvalidProductName() {

        LoanProductDTO dto = new LoanProductDTO();
        dto.productName = "";

        assertThrows(ResponseStatusException.class,
                () -> service.create(dto));
    }

    @Test
    void testInvalidAmountRange() {

        LoanProductDTO dto = new LoanProductDTO();
        dto.productName = "Loan";
        dto.minAmount = 1000.0;
        dto.maxAmount = 500.0;

        assertThrows(ResponseStatusException.class,
                () -> service.create(dto));
    }

    @Test
    void testDuplicateProduct() {

        LoanProductDTO dto = new LoanProductDTO();
        dto.productName = "Home Loan";
        dto.minAmount = 1000.0;
        dto.maxAmount = 5000.0;
        dto.annualInterestRate = 10.0;
        dto.allowedTenures = "12";

        when(repo.findByProductNameIgnoreCase("Home Loan"))
                .thenReturn(java.util.Optional.of(new com.neobank.entity.LoanProduct()));

        assertThrows(ResponseStatusException.class,
                () -> service.create(dto));
    }

    @Test
    void testValidProduct() {

        LoanProductDTO dto = new LoanProductDTO();
        dto.productName = "Home Loan";
        dto.minAmount = 1000.0;
        dto.maxAmount = 5000.0;
        dto.annualInterestRate = 10.0;
        dto.allowedTenures = "12";

        when(repo.findByProductNameIgnoreCase(anyString()))
                .thenReturn(java.util.Optional.empty());

        when(repo.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        assertNotNull(service.create(dto));
    }
}
