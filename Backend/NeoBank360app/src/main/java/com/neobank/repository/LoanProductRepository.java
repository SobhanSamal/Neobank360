package com.neobank.repository;

import com.neobank.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {

    Optional<LoanProduct> findByProductNameIgnoreCase(String productName);
}