package com.neobank.service;

import com.neobank.dto.TransactionRequest;
import com.neobank.dto.TransactionResponse;

import com.neobank.entity.Account;
import com.neobank.entity.Transaction;
import com.neobank.entity.User;

import com.neobank.repository.AccountRepository;
import com.neobank.repository.TransactionRepository;
import com.neobank.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Account testAccount;
    private TransactionRequest transactionRequest;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUser(testUser);
        testAccount.setAccountType(Account.AccountType.SAVING);
        testAccount.setBalance(new BigDecimal("5000.00"));

        transactionRequest = new TransactionRequest();
        transactionRequest.setAccountId(1L);
        transactionRequest.setAmount(new BigDecimal("1000.00"));
        transactionRequest.setDescription("Test transaction");

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAccount(testAccount);
        testTransaction.setType(Transaction.TransactionType.CREDIT);
        testTransaction.setAmount(new BigDecimal("1000.00"));
        testTransaction.setDescription("Test transaction");
        testTransaction.setBalanceAfter(new BigDecimal("6000.00"));

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("Should deposit money successfully")
    void testDepositSuccess() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(testAccount);

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(testTransaction);

        TransactionResponse response =
                transactionService.deposit(transactionRequest);

        assertNotNull(response);
        assertEquals("CREDIT", response.getType());
        assertEquals(0,
                new BigDecimal("1000.00")
                        .compareTo(response.getAmount()));

        verify(accountRepository, times(1))
                .save(any(Account.class));

        verify(transactionRepository, times(1))
                .save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should update account balance after deposit")
    void testDepositUpdatesBalance() {

        BigDecimal initialBalance = testAccount.getBalance();
        BigDecimal depositAmount = transactionRequest.getAmount();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(testTransaction);

        transactionService.deposit(transactionRequest);

        verify(accountRepository, times(1)).save(argThat(account ->
                account.getBalance()
                        .compareTo(initialBalance.add(depositAmount)) == 0
        ));
    }

    @Test
    @DisplayName("Should throw 404 when account not found for deposit")
    void testDepositAccountNotFound() {

        when(accountRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> transactionService.deposit(transactionRequest));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    @DisplayName("Should throw 403 when accessing other user's account")
    void testDepositAccessDenied() {

        User otherUser = new User();
        otherUser.setId(2L);

        testAccount.setUser(otherUser);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> transactionService.deposit(transactionRequest));

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    @DisplayName("Should withdraw money successfully")
    void testWithdrawSuccess() {

        testTransaction.setType(Transaction.TransactionType.DEBIT);
        testTransaction.setBalanceAfter(new BigDecimal("4000.00"));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        when(accountRepository.save(any(Account.class)))
                .thenReturn(testAccount);

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(testTransaction);

        TransactionResponse response =
                transactionService.withdraw(transactionRequest);

        assertNotNull(response);
        assertEquals("DEBIT", response.getType());

        verify(accountRepository).save(any(Account.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw exception for insufficient balance")
    void testWithdrawInsufficientBalance() {

        testAccount.setBalance(new BigDecimal("500.00"));
        transactionRequest.setAmount(new BigDecimal("1000.00"));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> transactionService.withdraw(transactionRequest));

        assertEquals(400, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("Insufficient balance"));
    }

    @Test
    @DisplayName("Should update balance after withdrawal")
    void testWithdrawUpdatesBalance() {

        BigDecimal initialBalance = new BigDecimal("5000.00");
        testAccount.setBalance(initialBalance);

        BigDecimal withdrawAmount = transactionRequest.getAmount();

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(testAccount));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(testTransaction);

        transactionService.withdraw(transactionRequest);

        verify(accountRepository).save(argThat(account ->
                account.getBalance()
                        .compareTo(initialBalance.subtract(withdrawAmount)) == 0
        ));
    }

    @Test
    @DisplayName("Should retrieve transaction history")
    void testGetTransactionHistorySuccess() {

        List<Transaction> transactions = List.of(testTransaction);

        when(transactionRepository.findByAccountIdOrderByTransactionDateDesc(
                eq(1L), any(Pageable.class)))
            .thenReturn(new PageImpl<>(transactions));

        List<TransactionResponse> responses =
                transactionService.getTransactionHistory(1L, 0, 20);

        assertEquals(1, responses.size());
    }
}