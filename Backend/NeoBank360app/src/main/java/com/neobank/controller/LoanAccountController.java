
package com.neobank.controller;

import com.neobank.dto.LoanAccountDTO;
import com.neobank.entity.User;
import com.neobank.repository.LoanAccountRepository;
import com.neobank.service.CurrentUserService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanAccountController {

    private final LoanAccountRepository repo;
    private final CurrentUserService currentUserService;

    public LoanAccountController(
            LoanAccountRepository repo,
            CurrentUserService currentUserService
    ) {
        this.repo = repo;
        this.currentUserService = currentUserService;
    }

    /* ✅ GET USER LOAN ACCOUNTS */
    @GetMapping("/my-accounts")
    @Transactional(readOnly = true)
    public List<LoanAccountDTO> getMyAccounts() {

        User user = currentUserService.getCurrentUser();

        return repo.findByUserId(user.getId()).stream()
                .map(account -> {
                    LoanAccountDTO dto = new LoanAccountDTO();
                    dto.setId(account.getId());
                    dto.setPrincipalAmount(account.getPrincipalAmount());
                    dto.setAnnualInterestRate(account.getAnnualInterestRate());
                    dto.setTenureMonths(account.getTenureMonths());
                    dto.setEmiAmount(account.getEmiAmount());
                    dto.setDisbursedAt(account.getDisbursedAt());
                    dto.setProductName(
                            account.getLoanApplication()
                                   .getLoanProduct()
                                   .getProductName()
                    );
                    return dto;
                })
                .toList();
    }
}
