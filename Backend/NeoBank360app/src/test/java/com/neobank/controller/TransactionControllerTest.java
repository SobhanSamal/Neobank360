package com.neobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.neobank.dto.TransactionRequest;
import com.neobank.dto.TransactionResponse;
import com.neobank.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionController Tests")
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(transactionController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should create credit transaction through deposit endpoint")
    void testDeposit() throws Exception {

        TransactionRequest request = transactionRequest();

        TransactionResponse response =
                transactionResponse("CREDIT", "1000.00", "6000.00");

        when(transactionService.deposit(any(TransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type", is("CREDIT")))
            .andExpect(jsonPath("$.amount", comparesEqualTo(1000.00)))
            .andExpect(jsonPath("$.balanceAfter",
                    comparesEqualTo(6000.00)));

        verify(transactionService)
                .deposit(any(TransactionRequest.class));
    }

    @Test
    @DisplayName("Should create debit transaction through withdraw endpoint")
    void testWithdraw() throws Exception {

        TransactionRequest request = transactionRequest();

        TransactionResponse response =
                transactionResponse("DEBIT", "1000.00", "4000.00");

        when(transactionService.withdraw(any(TransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/transactions/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type", is("DEBIT")))
            .andExpect(jsonPath("$.balanceAfter",
                    comparesEqualTo(4000.00)));

        verify(transactionService)
                .withdraw(any(TransactionRequest.class));
    }

    @Test
    @DisplayName("Should reject zero amount transaction request")
    void testTransactionValidationFailure() throws Exception {

        TransactionRequest request = transactionRequest();
        request.setAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/transactions/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(transactionService, never())
                .deposit(any());
    }

    @Test
    @DisplayName("Should fetch paginated transaction history")
    void testGetHistory() throws Exception {

        when(transactionService.getTransactionHistory(1L, 0, 20))
                .thenReturn(List.of(
                        transactionResponse("CREDIT", "1000.00", "6000.00")
                ));

        mockMvc.perform(get("/api/transactions/1?page=0&size=20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountId", is(1)))
            .andExpect(jsonPath("$[0].type", is("CREDIT")))
            .andExpect(jsonPath("$[0].accountNumber",
                    is("NB1234567890")));

        verify(transactionService)
                .getTransactionHistory(1L, 0, 20);
    }

    private TransactionRequest transactionRequest() {

        TransactionRequest request = new TransactionRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("1000.00"));
        request.setDescription("Salary");

        return request;
    }

    private TransactionResponse transactionResponse(
            String type,
            String amount,
            String balanceAfter
    ) {
        return new TransactionResponse(
                1L,
                1L,
                "NB1234567890",
                type,
                new BigDecimal(amount),
                "Salary",
                new BigDecimal(balanceAfter),
                LocalDateTime.now()
        );
    }
}