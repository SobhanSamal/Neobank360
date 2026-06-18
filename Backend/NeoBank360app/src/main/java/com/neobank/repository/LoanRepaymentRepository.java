package com.neobank.repository;

import com.neobank.entity.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {

    List<LoanRepayment> findByLoanAccountIdOrderByInstalmentNumber(Long accountId);
}