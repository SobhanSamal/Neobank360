package com.neobank.controller;

import com.neobank.dto.LoanProductDTO;
import com.neobank.service.LoanProductService;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans/products")
public class LoanProductController {

    private final LoanProductService service;

    public LoanProductController(LoanProductService service) {
        this.service = service;
    }

    /* ✅ ADMIN ONLY */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanProductDTO create(@RequestBody LoanProductDTO dto) {
    	System.out.println("LOAN ADDEDDDDD...................");
        return service.create(dto);
    }

    /* ✅ ALL USERS */
    @GetMapping
    public List<LoanProductDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public LoanProductDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }
}