package com.neobank.service;

import com.neobank.dto.LoanProductDTO;
import com.neobank.entity.LoanProduct;
import com.neobank.repository.LoanProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LoanProductService {

    private final LoanProductRepository repo;

    public LoanProductService(LoanProductRepository repo) {
        this.repo = repo;
    }

    /* ✅ CREATE PRODUCT */
    public LoanProductDTO create(LoanProductDTO dto) {
    	System.out.println("Hotteddddd.....");
    	System.out.println(dto.toString());
        // ✅ VALIDATION
        if (dto.productName == null || dto.productName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name required");
        }

        if (dto.minAmount <= 0 || dto.maxAmount <= dto.minAmount) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid amount range");
        }

        if (dto.annualInterestRate <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid interest rate");
        }

        if (dto.allowedTenures == null || dto.allowedTenures.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenures required");
        }

        // ✅ UNIQUE CHECK
        repo.findByProductNameIgnoreCase(dto.productName)
                .ifPresent(p -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Product already exists"
                    );
                });

        // ✅ DTO → ENTITY
        LoanProduct product = new LoanProduct();
        product.setProductName(dto.productName);
        product.setMinAmount(dto.minAmount);
        product.setMaxAmount(dto.maxAmount);
        product.setAnnualInterestRate(dto.annualInterestRate);
        product.setAllowedTenures(dto.allowedTenures);

        LoanProduct saved = repo.save(product);

        return map(saved);
    }

    /* ✅ GET ALL */
    public List<LoanProductDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    /* ✅ GET BY ID */
    public LoanProductDTO getById(Long id) {

        LoanProduct p = repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Product not found"
                        )
                );

        return map(p);
    }

    /* ✅ MAPPER */
    private LoanProductDTO map(LoanProduct p) {
        return new LoanProductDTO(
                p.getId(),
                p.getProductName(),
                p.getMinAmount(),
                p.getMaxAmount(),
                p.getAnnualInterestRate(),
                p.getAllowedTenures()
        );
    }
}
