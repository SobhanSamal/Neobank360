package com.neobank.service;
 
import com.neobank.dto.AccountResponse;
import com.neobank.dto.CreateAccountRequest;
import com.neobank.entity.Account;
import com.neobank.entity.User;
import com.neobank.repository.AccountRepository;
import com.neobank.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
 
@Service
public class AccountService {
 
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
 
    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }
 
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        User user = getAuthenticatedUser();
 
        String typeRaw = request.getAccountType();
        if (typeRaw == null || typeRaw.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "accountType is required");
        }
 
        Account.AccountType accountType = parseAccountType(typeRaw);
 
        if (accountRepository.existsByUserAndAccountType(user, accountType)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "You already have a " + accountType.name() + " account"
            );
        }
 
        Account account = new Account();
        account.setUser(user);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(generateUniqueAccountNumber());
 
        try {
            Account savedAccount = accountRepository.save(account);
 
            if (user.getDefaultAccountId() == null) {
                user.setDefaultAccountId(savedAccount.getId());
                userRepository.save(user);
            }
 
            return AccountResponse.from(savedAccount);
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "You already have a " + accountType.name() + " account"
            );
        }
    }
 
    private Account.AccountType parseAccountType(String typeRaw) {
        String normalized = typeRaw.trim().toUpperCase(Locale.ROOT).replace(" ", "_");
        return switch (normalized) {
            case "SAVING", "SAVINGS" -> Account.AccountType.SAVING;
            case "CURRENT" -> Account.AccountType.CURRENT;
            default -> throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid accountType. Use SAVING or CURRENT"
            );
        };
    }
 
    @Transactional(readOnly = true)
    public List<AccountResponse> getMyAccounts() {
        User user = getAuthenticatedUser();
        return accountRepository.findByUser(user)
            .stream()
            .map(AccountResponse::from)
            .toList();
    }
 
    @Transactional(readOnly = true)
    public AccountResponse getMyAccountById(Long id) {
        User user = getAuthenticatedUser();
 
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
 
        if (account.getUser() == null || !account.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this account");
        }
 
        return AccountResponse.from(account);
    }
 
    /**
     * Admin-only: Get all accounts in the system
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
            .stream()
            .map(AccountResponse::from)
            .toList();
    }
 
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }
 
    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            String uuidPart = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT).substring(0, 18);
            accountNumber = "NB" + uuidPart;
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }
}
 