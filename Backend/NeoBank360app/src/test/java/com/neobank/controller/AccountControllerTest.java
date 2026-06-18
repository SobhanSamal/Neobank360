package com.neobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neobank.dto.AccountResponse;
import com.neobank.dto.CreateAccountRequest;
import com.neobank.service.AccountService;

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
@DisplayName("AccountController Tests")
class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should create account and return 201")
    void testCreateAccount() throws Exception {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType("SAVING");

        AccountResponse response =
                accountResponse(1L, "NB1234567890", "SAVING", "0.00");

        when(accountService.createAccount(any(CreateAccountRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.accountNumber", is("NB1234567890")))
            .andExpect(jsonPath("$.accountType", is("SAVING")))
            .andExpect(jsonPath("$.balance", comparesEqualTo(0.00)));

        verify(accountService).createAccount(any(CreateAccountRequest.class));
    }

    @Test
    @DisplayName("Should reject account creation without account type")
    void testCreateAccountValidationFailure() throws Exception {

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType("");

        mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(accountService, never()).createAccount(any());
    }

    @Test
    @DisplayName("Should list authenticated user's accounts")
    void testGetAccounts() throws Exception {

        when(accountService.getMyAccounts()).thenReturn(List.of(
                accountResponse(1L, "NB1234567890", "SAVING", "5000.00")
        ));

        mockMvc.perform(get("/api/accounts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountNumber", is("NB1234567890")))
            .andExpect(jsonPath("$[0].balance",
                    comparesEqualTo(5000.00)));

        verify(accountService).getMyAccounts();
    }

    @Test
    @DisplayName("Should get account by id")
    void testGetAccountById() throws Exception {

        when(accountService.getMyAccountById(1L))
                .thenReturn(accountResponse(
                        1L, "NB1234567890", "CURRENT", "2500.00"
                ));

        mockMvc.perform(get("/api/accounts/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accountType", is("CURRENT")));

        verify(accountService).getMyAccountById(1L);
    }

    @Test
    @DisplayName("Should list all accounts for admin dashboard endpoint")
    void testGetAllAccounts() throws Exception {

        when(accountService.getAllAccounts()).thenReturn(List.of(
                accountResponse(1L, "NB1234567890", "SAVING", "5000.00"),
                accountResponse(2L, "NB0987654321", "CURRENT", "7000.00")
        ));

        mockMvc.perform(get("/api/accounts/admin/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].accountType", is("SAVING")))
            .andExpect(jsonPath("$[1].accountType", is("CURRENT")));

        verify(accountService).getAllAccounts();
    }

    private AccountResponse accountResponse(
            Long id,
            String number,
            String type,
            String balance
    ) {
        return new AccountResponse(
                id,
                number,
                type,
                new BigDecimal(balance)
        );
    }
}