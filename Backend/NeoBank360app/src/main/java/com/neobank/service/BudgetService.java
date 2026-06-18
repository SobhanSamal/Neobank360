package com.neobank.service;
 
import com.neobank.dto.BudgetRequestDTO;
import com.neobank.dto.BudgetResponseDTO;
import com.neobank.dto.BudgetSummaryDTO;
import com.neobank.entity.Budget;
import com.neobank.entity.BudgetCategory;
import com.neobank.entity.User;
import com.neobank.repository.BudgetRepository;
import com.neobank.repository.TransactionRepository;
import com.neobank.repository.UserRepository;
 
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
 
@Service
public class BudgetService {
 
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
 
    public BudgetService(
            BudgetRepository budgetRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository
    ) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }
 
    /* =======================
       CREATE BUDGET
       ======================= */
    @Transactional
    public BudgetResponseDTO create(BudgetRequestDTO request) {
 
        User user = getAuthenticatedUser();
 
        // ✅ ✅ ✅ FIX ADDED (SRS BR-01)
        if (request.getLimitAmount() == null ||
                request.getLimitAmount().compareTo(BigDecimal.ZERO) <= 0) {
 
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Budget limit must be greater than zero"
            );
        }
 
        LocalDate budgetMonth =
                YearMonth.parse(request.getBudgetMonth()).atDay(1);
 
        BudgetCategory category = request.getCategory();
 
        budgetRepository
                .findByUserAndCategoryAndBudgetMonth(
                        user,
                        category,
                        budgetMonth
                )
                .ifPresent(b -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Budget already exists for this category/month"
                    );
                });
 
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setBudgetMonth(budgetMonth);
        budget.setLimitAmount(request.getLimitAmount());
 
        return BudgetResponseDTO.from(
                budgetRepository.save(budget)
        );
    }
 
    /* =======================
       DASHBOARD SUMMARY
       ======================= */
    @Transactional(readOnly = true)
    public List<BudgetSummaryDTO> getSummary(String month) {
 
        User user = getAuthenticatedUser();
 
        LocalDate startDate =
                YearMonth.parse(month).atDay(1);
        LocalDate endDate =
                startDate.plusMonths(1);
 
        List<Budget> budgets =
                budgetRepository
                        .findByUserAndBudgetMonthOrderByCategoryAsc(
                                user,
                                startDate
                        );
 
        return budgets.stream()
                .map(budget -> {
 
                    BigDecimal spent =
                            transactionRepository
                                    .getTotalSpentByCategoryAndMonth(
                                            user,
                                            budget.getCategory().name(),
                                            startDate.atStartOfDay(),
                                            endDate.atStartOfDay()
                                    );
 
                    if (spent == null) spent = BigDecimal.ZERO;
 
                    BigDecimal remaining =
                            budget.getLimitAmount().subtract(spent);
                    if (remaining.compareTo(BigDecimal.ZERO) < 0) {
                        remaining = BigDecimal.ZERO;
                    }
 
                    BigDecimal utilization =
                            budget.getLimitAmount().compareTo(BigDecimal.ZERO) > 0
                                    ? spent.multiply(BigDecimal.valueOf(100))
                                            .divide(
                                                    budget.getLimitAmount(),
                                                    2,
                                                    RoundingMode.HALF_UP
                                            )
                                    : BigDecimal.ZERO;
 
                    return new BudgetSummaryDTO(
                            budget.getCategory().name(),
                            month,
                            budget.getLimitAmount(),
                            spent,
                            remaining,
                            utilization
                    );
                })
                .toList();
    }
 
    /* =======================
       LIST ALL USER BUDGETS
       ======================= */
    @Transactional(readOnly = true)
    public List<BudgetResponseDTO> listMine() {
 
        User user = getAuthenticatedUser();
 
        return budgetRepository
                .findByUserOrderByBudgetMonthDescCategoryAsc(user)
                .stream()
                .map(BudgetResponseDTO::from)
                .toList();
    }
 
    /* =======================
       DELETE BUDGET
       ======================= */
    @Transactional
    public void delete(Long id) {
 
        User user = getAuthenticatedUser();
 
        Budget budget = budgetRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Budget not found"
                        )
                );
 
        if (!budget.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied"
            );
        }
 
        budgetRepository.delete(budget);
    }
 
    /* =======================
       AUTHENTICATED USER
       ======================= */
    private User getAuthenticatedUser() {
 
        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();
 
        if (auth == null || auth.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized"
            );
        }
 
        return userRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "User not found"
                        )
                );
    }
}
 