package com.neobank.service;

import com.neobank.dto.TransactionRequest;
import com.neobank.dto.TransactionResponse;
import com.neobank.entity.Account;
import com.neobank.entity.Transaction;
import com.neobank.repository.AccountRepository;
import com.neobank.repository.TransactionRepository;
import com.neobank.repository.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            UserRepository userRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    /* =======================
       DEPOSIT
       ======================= */
    @Transactional
    public TransactionResponse deposit(TransactionRequest request) {

        Account account = accountRepository
                .findById(request.getAccountId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Account not found"
                        )
                );

        BigDecimal newBalance =
                account.getBalance().add(request.getAmount());

        account.setBalance(newBalance);

        Transaction txn = new Transaction();
        txn.setAccount(account);
        txn.setAmount(request.getAmount());
        txn.setType(Transaction.TransactionType.CREDIT);
        txn.setDescription(request.getDescription());
        txn.setBalanceAfter(newBalance);

        accountRepository.save(account);
        return TransactionResponse.from(
                transactionRepository.save(txn)
        );
    }

    /* =======================
       WITHDRAW
       ======================= */
    @Transactional
    public TransactionResponse withdraw(TransactionRequest request) {

        Account account = accountRepository
                .findById(request.getAccountId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Account not found"
                        )
                );

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insufficient balance"
            );
        }

        BigDecimal newBalance =
                account.getBalance().subtract(request.getAmount());

        account.setBalance(newBalance);

        Transaction txn = new Transaction();
        txn.setAccount(account);
        txn.setAmount(request.getAmount());
        txn.setType(Transaction.TransactionType.DEBIT);
        txn.setDescription(request.getDescription());
        txn.setBalanceAfter(newBalance);

        accountRepository.save(account);
        return TransactionResponse.from(
                transactionRepository.save(txn)
        );
    }

    /* =======================
       TRANSACTION HISTORY ✅ FIX
       ======================= */
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactionHistory(
            Long accountId,
            int page,
            int size
    ) {
        return transactionRepository
                .findByAccountIdOrderByTransactionDateDesc(
                        accountId,
                        PageRequest.of(page, size)
                )
                .map(TransactionResponse::from)
                .getContent();
    }
}