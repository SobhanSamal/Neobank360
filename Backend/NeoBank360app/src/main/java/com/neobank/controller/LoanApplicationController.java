package com.neobank.controller;

import com.neobank.dto.*;
import com.neobank.service.LoanApplicationService;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanApplicationController {

    private final LoanApplicationService service;

    public LoanApplicationController(LoanApplicationService service) {
        this.service = service;
    }

    /* ✅ APPLY LOAN */
    @PostMapping("/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanApplicationResponseDTO apply(
            @RequestBody LoanApplicationRequestDTO dto
    ) {
        return service.apply(dto);
    }

    @GetMapping({"/my", "/my-applications"})
    public List<LoanApplicationResponseDTO> getMyApplications() {
        return service.getMyApplications();
    }

    /* ✅ DAY‑24: GET ALL APPLICATIONS (ADMIN) */
    @GetMapping({"/admin", "/admin/applications"})
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<LoanApplicationResponseDTO> getAll(
            @RequestParam(required = false) String status
    ) {
        return service.getAll(status);
    }

    /* ✅ APPROVE / REJECT (ADMIN ONLY) */
    @PutMapping("/{id}/decision")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void decide(
            @PathVariable Long id,
            @RequestBody LoanDecisionDTO dto
    ) {
        service.decide(id, dto);
    }
}