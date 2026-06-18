package com.neobank.controller;

import com.neobank.dto.RepaymentScheduleDTO;
import com.neobank.service.LoanRepaymentService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanRepaymentController {

    private final LoanRepaymentService service;

    public LoanRepaymentController(LoanRepaymentService service) {
        this.service = service;
    }

    /* ✅ GET REPAYMENT SCHEDULE */
    @GetMapping("/{accountId}/repayments")
    public List<RepaymentScheduleDTO> getSchedule(
            @PathVariable Long accountId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {
        return service.getSchedule(accountId, status, page, size);
    }

    /* ✅ PAY EMI */
    @PatchMapping("/{accountId}/repayments/{repaymentId}/pay")
    public void pay(
            @PathVariable Long accountId,
            @PathVariable Long repaymentId
    ) {
        service.pay(accountId, repaymentId);
    }

    @PatchMapping("/repayments/{repaymentId}/pay")
    public void payLegacy(@PathVariable Long repaymentId) {
        service.pay(repaymentId);
    }
}