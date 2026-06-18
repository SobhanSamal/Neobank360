package com.neobank.controller;
 
import com.neobank.dto.BudgetRequestDTO;
import com.neobank.dto.BudgetResponseDTO;
import com.neobank.dto.BudgetSummaryDTO;
import com.neobank.service.BudgetService;
 
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
 
    private final BudgetService budgetService;
 
    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }
 
    /* =======================
       CREATE BUDGET
       ======================= */
    @PostMapping
    public ResponseEntity<BudgetResponseDTO> create(
            @Valid @RequestBody BudgetRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(budgetService.create(request));
    }
 
    /* =======================
       MONTHLY BUDGET SUMMARY
       ======================= */
    @GetMapping("/summary/{month}")
    public ResponseEntity<List<BudgetSummaryDTO>> getSummary(
            @PathVariable String month
    ) {
        return ResponseEntity.ok(
                budgetService.getSummary(month)
        );
    }
 
    /* =======================
       LIST ALL USER BUDGETS
       ======================= */
    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> listMine() {
        return ResponseEntity.ok(
                budgetService.listMine()
        );
    }
 
    /* =======================
       DELETE BUDGET
       ======================= */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
 