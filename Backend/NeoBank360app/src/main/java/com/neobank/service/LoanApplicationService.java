package com.neobank.service;

import com.neobank.dto.*;
import com.neobank.entity.*;
import com.neobank.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class LoanApplicationService {

    private final LoanApplicationRepository repo;
    private final LoanProductRepository productRepo;
    private final UserRepository userRepo;

    private final LoanAccountService loanAccountService;
    private final RepaymentScheduleService repaymentScheduleService;

    public LoanApplicationService(
            LoanApplicationRepository repo,
            LoanProductRepository productRepo,
            UserRepository userRepo,
            LoanAccountService loanAccountService,
            RepaymentScheduleService repaymentScheduleService
    ) {
        this.repo = repo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.loanAccountService = loanAccountService;
        this.repaymentScheduleService = repaymentScheduleService;
    }

    /* ✅ APPLY LOAN */
    public LoanApplicationResponseDTO apply(LoanApplicationRequestDTO dto) {

        Long userId = getCurrentUserId();

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        LoanProduct product = productRepo.findById(dto.productId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        /* ✅ Amount validation */
        if (dto.requestedAmount < product.getMinAmount()
                || dto.requestedAmount > product.getMaxAmount()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid amount range");
        }

        /* ✅ Tenure validation */
        boolean validTenure = Arrays.stream(product.getAllowedTenures().split(","))
                .anyMatch(t -> Integer.parseInt(t.trim()) == dto.requestedTenureMonths);

        if (!validTenure) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid tenure");
        }

        /* ✅ Duplicate check */
        boolean exists = repo.existsByUserIdAndLoanProductIdAndStatus(
                userId,
                dto.productId,
                LoanApplication.Status.PENDING
        );

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Duplicate application");
        }

        LoanApplication app = new LoanApplication();
        app.setUser(user);
        app.setLoanProduct(product);
        app.setRequestedAmount(dto.requestedAmount);
        app.setRequestedTenureMonths(dto.requestedTenureMonths);

        LoanApplication saved = repo.save(app);

        LoanApplicationResponseDTO res = new LoanApplicationResponseDTO();
        res.id = saved.getId();
        res.status = saved.getStatus().name();
        res.message = "Application submitted";

        return res;
    }

    /* ✅ ✅ ✅ DAY-24 FIX */
    @Transactional
    public List<LoanApplicationResponseDTO> getAll(String status) {

        return repo.findAll().stream()
                .filter(app -> status == null || status.isBlank()
                        || app.getStatus().name().equalsIgnoreCase(status))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanApplicationResponseDTO> getMyApplications() {

        Long userId = getCurrentUserId();

        return repo.findByUserIdOrderByIdDesc(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* ✅ APPROVE / REJECT */
    @Transactional
    public void decide(Long id, LoanDecisionDTO dto) {

        LoanApplication app = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        if (app.getStatus() != LoanApplication.Status.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Already processed");
        }

        if (!dto.getDecision().equals("APPROVED")
                && !dto.getDecision().equals("REJECTED")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid decision");
        }

        if (dto.getDecision().equals("APPROVED")) {

            app.setStatus(LoanApplication.Status.APPROVED);

            LoanAccount account = loanAccountService.createAccount(app);

            repaymentScheduleService.generateSchedule(account);

        } else {
            app.setStatus(LoanApplication.Status.REJECTED);
        }

        app.setAdminRemarks(dto.getRemarks());
        app.setDecidedAt(LocalDateTime.now());

        repo.save(app);
    }

    /* ✅ GET CURRENT USER */
    private Long getCurrentUserId() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return user.getId();
    }

    private LoanApplicationResponseDTO mapToResponse(LoanApplication app) {

        LoanApplicationResponseDTO dto = new LoanApplicationResponseDTO();

        dto.id = app.getId();
        dto.status = app.getStatus().name();
        dto.message = "Fetched";
        dto.userEmail = app.getUser().getEmail();
        dto.productName = app.getLoanProduct().getProductName();
        dto.requestedAmount = app.getRequestedAmount();
        dto.requestedTenureMonths = app.getRequestedTenureMonths();

        return dto;
    }
}