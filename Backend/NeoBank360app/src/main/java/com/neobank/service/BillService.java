package com.neobank.service;

import com.neobank.dto.*;
import com.neobank.entity.*;
import com.neobank.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final RewardService rewardService;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    public BillService(
            BillRepository billRepository,
            UserRepository userRepository,
            RewardService rewardService,
            AccountRepository accountRepository,
            TransactionService transactionService
    ) {
        this.billRepository = billRepository;
        this.userRepository = userRepository;
        this.rewardService = rewardService;
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
    }

    /* =========================
       CREATE BILL ✅ UPDATED
    ========================= */
    @Transactional
    public BillResponseDTO create(BillRequestDTO request) {

        User user = getAuthenticatedUser();

        /* ✅ ACCOUNT VALIDATION */
        if (request.getAccountId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Account is required"
            );
        }

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Account not found"
                        )
                );

        /* ✅ SECURITY CHECK */
        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }

        Bill bill = new Bill();

        /* ✅ LINK ACCOUNT */
        bill.setAccount(account);

        bill.setUser(user);
        bill.setBillerName(request.getBillerName());
        bill.setAmount(request.getAmount());
        bill.setDueDate(request.getDueDate());
        bill.setCategory(request.getCategory());
        bill.setStatus(Bill.BillStatus.PENDING);

        return toResponse(billRepository.save(bill));
    }

    /* =========================
       LIST USER BILLS
    ========================= */
    @Transactional(readOnly = true)
    public List<BillResponseDTO> listMine() {

        User user = getAuthenticatedUser();

        return billRepository.findByUserOrderByDueDateAsc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /* =========================
       GET BILL BY ID
    ========================= */
    @Transactional(readOnly = true)
    public BillResponseDTO getById(Long id) {

        User user = getAuthenticatedUser();
        Bill bill = findOwnedBill(id, user);

        return toResponse(bill);
    }

    /* =========================
       UPDATE STATUS ✅ UPDATED
    ========================= */
    @Transactional
    public BillResponseDTO updateStatus(Long id, String requestedStatus) {

        User user = getAuthenticatedUser();
        Bill bill = findOwnedBill(id, user);

        Bill.BillStatus newStatus = parseStatus(requestedStatus);

        if (bill.getStatus() != Bill.BillStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PENDING bills can be updated"
            );
        }

        if (newStatus == Bill.BillStatus.PAID) {

            /* ✅ USE BILL ACCOUNT */
            Account account = bill.getAccount();

            /* ✅ BALANCE CHECK */
            if (account.getBalance().compareTo(bill.getAmount()) < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient balance"
                );
            }

            /* ✅ WITHDRAW */
            TransactionRequest req = new TransactionRequest();
            req.setAccountId(account.getId());
            req.setAmount(bill.getAmount());
            req.setDescription("BILL_PAYMENT - " + bill.getCategory());

            transactionService.withdraw(req);

            /* ✅ REWARD CALCULATION */
            BigDecimal points = bill.getAmount()
                    .divide(BigDecimal.valueOf(100));

            rewardService.addPoints(user, points);

            bill.setPaidAt(LocalDateTime.now());
        }

        bill.setStatus(newStatus);

        return toResponse(billRepository.save(bill));
    }

    /* =========================
       HELPERS
    ========================= */

    private Bill findOwnedBill(Long id, User user) {

        Bill bill = billRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Bill not found"
                        )
                );

        if (!bill.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }

        return bill;
    }

    private Bill.BillStatus parseStatus(String status) {

        try {
            return Bill.BillStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status"
            );
        }
    }

    private BillResponseDTO toResponse(Bill bill) {
        return BillResponseDTO.from(bill, true);
    }

    private User getAuthenticatedUser() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED)
                );
    }
}
