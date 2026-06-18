package com.neobank.repository;

import com.neobank.dto.PendingApprovalDTO;
import com.neobank.entity.LoanApplication;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface PendingApprovalRepository extends Repository<LoanApplication, Long> {

    @Query("""
        SELECT new com.neobank.dto.PendingApprovalDTO(
            l.id,
            'LOAN_APPLICATION',
            l.user.fullName,
            l.loanProduct.productName,
            l.requestedAmount,
            l.appliedAt
        )
        FROM LoanApplication l
        WHERE l.status = 'PENDING'
        ORDER BY l.appliedAt ASC
    """)
    List<PendingApprovalDTO> findPendingApprovals();
}