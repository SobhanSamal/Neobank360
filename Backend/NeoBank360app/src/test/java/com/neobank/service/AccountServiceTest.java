package com.neobank.service;

import com.neobank.dto.AccountResponse;
import com.neobank.dto.CreateAccountRequest;
import com.neobank.entity.Account;
import com.neobank.entity.User;
import com.neobank.repository.AccountRepository;
import com.neobank.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private Account savingAccount;
    private Account currentAccount;
    private CreateAccountRequest createSavingRequest;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");
        testUser.setRole(User.Role.CUSTOMER);

        savingAccount = new Account();
        savingAccount.setId(1L);
        savingAccount.setUser(testUser);
        savingAccount.setAccountType(Account.AccountType.SAVING);
        savingAccount.setBalance(BigDecimal.ZERO);
        savingAccount.setAccountNumber("NB123456789012345678");

        currentAccount = new Account();
        currentAccount.setId(2L);
        currentAccount.setUser(testUser);
        currentAccount.setAccountType(Account.AccountType.CURRENT);
        currentAccount.setBalance(BigDecimal.ZERO);
        currentAccount.setAccountNumber("NB987654321098765432");

        createSavingRequest = new CreateAccountRequest();
        createSavingRequest.setAccountType("SAVING");

        SecurityContextHolder.setContext(securityContext);

        lenient().when(securityContext.getAuthentication())
                .thenReturn(authentication);

        lenient().when(authentication.getName())
                .thenReturn("test@example.com");

        lenient().when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("Should create account successfully")
    void testCreateAccountSuccess() {

        when(accountRepository.existsByUserAndAccountType(
                testUser,
                Account.AccountType.SAVING))
            .thenReturn(false);

        when(accountRepository.existsByAccountNumber(anyString()))
            .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
            .thenReturn(savingAccount);

        AccountResponse response =
                accountService.createAccount(createSavingRequest);

        assertNotNull(response);
        assertEquals(
                Account.AccountType.SAVING.name(),
                response.getAccountType()
        );

        verify(accountRepository, times(1))
                .save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception when account type is null")
    void testCreateAccountNullType() {

        createSavingRequest.setAccountType(null);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> accountService.createAccount(createSavingRequest));

        assertEquals("accountType is required", exception.getReason());
    }

    @Test
    @DisplayName("Should throw exception when account type is blank")
    void testCreateAccountBlankType() {

        createSavingRequest.setAccountType("   ");

        assertThrows(ResponseStatusException.class,
                () -> accountService.createAccount(createSavingRequest));
    }

    @Test
    @DisplayName("Should throw 409 CONFLICT when SAVING account already exists")
    void testCreateAccountDuplicateSaving() {

        when(accountRepository.existsByUserAndAccountType(
                testUser,
                Account.AccountType.SAVING))
            .thenReturn(true);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> accountService.createAccount(createSavingRequest));

        assertEquals(409, exception.getStatusCode().value());
        assertTrue(exception.getReason()
                .contains("already have a SAVING account"));
    }

    @Test
    @DisplayName("Should throw 409 CONFLICT when CURRENT account already exists")
    void testCreateAccountDuplicateCurrent() {

        CreateAccountRequest currentRequest = new CreateAccountRequest();
        currentRequest.setAccountType("CURRENT");

        when(accountRepository.existsByUserAndAccountType(
                testUser,
                Account.AccountType.CURRENT))
            .thenReturn(true);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> accountService.createAccount(currentRequest));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException as 409 CONFLICT")
    void testCreateAccountDataIntegrityViolation() {

        when(accountRepository.existsByUserAndAccountType(
                testUser,
                Account.AccountType.SAVING))
            .thenReturn(false);

        when(accountRepository.existsByAccountNumber(anyString()))
            .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
            .thenThrow(new DataIntegrityViolationException("Duplicate key violation"));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> accountService.createAccount(createSavingRequest));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    @DisplayName("Should parse account type case-insensitively")
    void testParseAccountTypeCaseInsensitive() {

        createSavingRequest.setAccountType("saving");

        when(accountRepository.existsByUserAndAccountType(
                testUser,
                Account.AccountType.SAVING))
            .thenReturn(false);

        when(accountRepository.existsByAccountNumber(anyString()))
            .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
            .thenReturn(savingAccount);

        AccountResponse response =
                accountService.createAccount(createSavingRequest);

        assertNotNull(response);

        verify(accountRepository, times(1))
                .save(any(Account.class));
    }

    @Test
    @DisplayName("Should parse SAVINGS as SAVING type")
    void testParseAccountTypeSavings() {

        createSavingRequest.setAccountType("SAVINGS");

        when(accountRepository.existsByUserAndAccountType(
                testUser,
                Account.AccountType.SAVING))
            .thenReturn(false);

        when(accountRepository.existsByAccountNumber(anyString()))
            .thenReturn(false);

        when(accountRepository.save(any(Account.class)))
            .thenReturn(savingAccount);

        AccountResponse response =
                accountService.createAccount(createSavingRequest);

        assertNotNull(response);

        verify(accountRepository, times(1))
                .save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid account type")
    void testParseAccountTypeInvalid() {

        createSavingRequest.setAccountType("INVALID");

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> accountService.createAccount(createSavingRequest));

        assertEquals(
                "Invalid accountType. Use SAVING or CURRENT",
                exception.getReason()
        );
    }

    @Test
    @DisplayName("Should retrieve all accounts for authenticated user")
    void testGetMyAccountsSuccess() {

        List<Account> accounts =
                List.of(savingAccount, currentAccount);

        when(accountRepository.findByUser(testUser))
                .thenReturn(accounts);

        List<AccountResponse> responses =
                accountService.getMyAccounts();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(accountRepository, times(1))
                .findByUser(testUser);
    }

    @Test
    @DisplayName("Should return empty list when user has no accounts")
    void testGetMyAccountsEmpty() {

        when(accountRepository.findByUser(testUser))
                .thenReturn(List.of());

        List<AccountResponse> responses =
                accountService.getMyAccounts();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Should retrieve account by ID when authorized")
    void testGetMyAccountByIdSuccess() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(savingAccount));

        AccountResponse response =
                accountService.getMyAccountById(1L);

        assertNotNull(response);
        assertEquals("SAVING", response.getAccountType());
    }

    @Test
    @DisplayName("Should throw 404 when account not found")
    void testGetMyAccountByIdNotFound() {

        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> accountService.getMyAccountById(999L));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    @DisplayName("Should throw 403 FORBIDDEN when accessing other user's account")
    void testGetMyAccountByIdForbidden() {

        User otherUser = new User();
        otherUser.setId(2L);

        savingAccount.setUser(otherUser);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(savingAccount));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> accountService.getMyAccountById(1L));

        assertEquals(403, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("not allowed"));
    }

    @Test
    @DisplayName("Should retrieve all accounts for admin")
    void testGetAllAccountsSuccess() {

        List<Account> allAccounts =
                List.of(savingAccount, currentAccount);

        when(accountRepository.findAll())
                .thenReturn(allAccounts);

        List<AccountResponse> responses =
                accountService.getAllAccounts();

        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(accountRepository, times(1))
                .findAll();
    }
}