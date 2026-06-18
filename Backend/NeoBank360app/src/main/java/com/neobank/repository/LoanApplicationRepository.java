package com.neobank.repository;


 

import com.neobank.entity.LoanApplication;

import org.springframework.data.jpa.repository.JpaRepository;


 

import java.util.List;


 

public interface LoanApplicationRepository

extends JpaRepository<LoanApplication, Long> {


 

boolean existsByUserIdAndLoanProductIdAndStatus(

Long userId,

Long productId,

LoanApplication.Status status

);


 

List<LoanApplication> findByUserIdOrderByIdDesc(Long userId);

}


 