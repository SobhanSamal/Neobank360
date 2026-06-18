package com.neobank.controller;


import com.neobank.dto.TransactionRequest;
import com.neobank.dto.TransactionResponse;
import com.neobank.service.TransactionService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /* =======================
       DEPOSIT
       ======================= */
    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody TransactionRequest request
    ) {
        return ResponseEntity.ok(
                transactionService.deposit(request)
        );
    }

    /* =======================
       WITHDRAW
       ======================= */
    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody TransactionRequest request
    ) {
        return ResponseEntity.ok(
                transactionService.withdraw(request)
        );
    }

    /* =======================
       TRANSACTION HISTORY (PAGINATED)
       ======================= */
    @GetMapping("/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getHistory(
            @PathVariable Long accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                transactionService.getTransactionHistory(
                        accountId, page, size
                )
        );
    }
}