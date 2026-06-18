package com.neobank.service;

import com.neobank.entity.*;
import com.neobank.repository.*;
import com.neobank.util.EmiCalculatorUtil;
import org.springframework.stereotype.Service;

@Service
public class LoanAccountService {

    private final LoanAccountRepository repo;

    public LoanAccountService(LoanAccountRepository repo) {
        this.repo = repo;
    }

    public LoanAccount createAccount(LoanApplication app) {

        double emi = EmiCalculatorUtil.calculateEMI(
                app.getRequestedAmount(),
                app.getLoanProduct().getAnnualInterestRate(),
                app.getRequestedTenureMonths()
        );

        LoanAccount acc = new LoanAccount();
        acc.setLoanApplication(app);
        acc.setUser(app.getUser());
        acc.setPrincipalAmount(app.getRequestedAmount());
        acc.setAnnualInterestRate(app.getLoanProduct().getAnnualInterestRate());
        acc.setTenureMonths(app.getRequestedTenureMonths());
        acc.setEmiAmount(emi);

        return repo.save(acc);
    }
}