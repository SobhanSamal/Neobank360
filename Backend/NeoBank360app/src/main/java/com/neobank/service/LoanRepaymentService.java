package com.neobank.service;

import com.neobank.dto.RepaymentScheduleDTO;
import com.neobank.entity.LoanAccount;
import com.neobank.entity.LoanRepayment;
import com.neobank.entity.User;
import com.neobank.repository.LoanAccountRepository;
import com.neobank.repository.LoanRepaymentRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanRepaymentService {

    private final LoanRepaymentRepository repo;
    private final LoanAccountRepository loanAccountRepo;
    private final CurrentUserService currentUserService;

    public LoanRepaymentService(
            LoanRepaymentRepository repo,
            LoanAccountRepository loanAccountRepo,
            CurrentUserService currentUserService
    ) {
        this.repo = repo;
        this.loanAccountRepo = loanAccountRepo;
        this.currentUserService = currentUserService;
    }

    /* ✅ FIXED METHOD */
    @Transactional
    public List<RepaymentScheduleDTO> getSchedule(Long accountId) {
        return getSchedule(accountId, null, 0, 100);
    }

    @Transactional
    public List<RepaymentScheduleDTO> getSchedule(Long accountId, String status, int page, int size) {

        LoanAccount account = loanAccountRepo.findById(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan account not found"));

        assertOwner(account);

        List<LoanRepayment> list =
                repo.findByLoanAccountIdOrderByInstalmentNumber(accountId);

        // ✅ Overdue update
        list.forEach(r -> {
            if (r.getPaymentStatus() == LoanRepayment.Status.PENDING &&
                    r.getDueDate().isBefore(LocalDate.now())) {

                r.setPaymentStatus(LoanRepayment.Status.OVERDUE);
                repo.save(r);
            }
        });

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);

        return list.stream()
                .filter(r -> status == null || status.isBlank()
                        || r.getPaymentStatus().name().equalsIgnoreCase(status))
                .skip((long) safePage * safeSize)
                .limit(safeSize)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /* ✅ PAY EMI */
    @Transactional
    public void pay(Long accountId, Long repaymentId) {

        LoanRepayment r = repo.findById(repaymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Repayment not found"));

        if (!r.getLoanAccount().getId().equals(accountId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Repayment does not belong to loan account");
        }

        pay(r);
    }

    @Transactional
    public void pay(Long repaymentId) {

        LoanRepayment r = repo.findById(repaymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Repayment not found"));

        pay(r);
    }

    private void pay(LoanRepayment r) {

        assertOwner(r.getLoanAccount());

        if (r.getPaymentStatus() == LoanRepayment.Status.PAID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already paid");
        }

        r.setPaymentStatus(LoanRepayment.Status.PAID);
        r.setPaymentDate(LocalDateTime.now());

        repo.save(r);
    }

    /* ✅ DTO MAPPING */
    private RepaymentScheduleDTO mapToDTO(LoanRepayment r) {

        RepaymentScheduleDTO dto = new RepaymentScheduleDTO();

        dto.id = r.getId();
        dto.instalmentNumber = r.getInstalmentNumber();
        dto.dueDate = r.getDueDate();
        dto.emiAmount = r.getEmiAmount();
        dto.principalComponent = r.getPrincipalComponent();
        dto.interestComponent = r.getInterestComponent();
        dto.paymentStatus = r.getPaymentStatus().name();

        return dto;
    }

    private void assertOwner(LoanAccount account) {
        User currentUser = currentUserService.getCurrentUser();

        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }
}