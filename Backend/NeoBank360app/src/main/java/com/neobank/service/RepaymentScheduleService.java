package com.neobank.service;

import com.neobank.entity.*;
import com.neobank.repository.LoanRepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class RepaymentScheduleService {

    private final LoanRepaymentRepository repo;

    public RepaymentScheduleService(LoanRepaymentRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void generateSchedule(LoanAccount account) {

        double balance = account.getPrincipalAmount();
        double rate = account.getAnnualInterestRate() / 12 / 100;
        double principalPaid = 0;

        LocalDate disbursedDate = account.getDisbursedAt() == null
                ? LocalDate.now()
                : account.getDisbursedAt().toLocalDate();

        for (int i = 1; i <= account.getTenureMonths(); i++) {

            double interest = Math.round(balance * rate * 100) / 100.0;
            double principal = Math.round((account.getEmiAmount() - interest) * 100) / 100.0;
            double emi = account.getEmiAmount();

            if (i == account.getTenureMonths()) {
                principal = Math.round((account.getPrincipalAmount() - principalPaid) * 100) / 100.0;
                interest = Math.round(balance * rate * 100) / 100.0;
                emi = Math.round((principal + interest) * 100) / 100.0;
            }

            balance -= principal;
            principalPaid += principal;

            LoanRepayment r = new LoanRepayment();
            r.setLoanAccount(account);
            r.setInstalmentNumber(i);
            r.setDueDate(disbursedDate.plusMonths(i));
            r.setEmiAmount(emi);
            r.setPrincipalComponent(principal);
            r.setInterestComponent(interest);

            repo.save(r);
        }
    }
}