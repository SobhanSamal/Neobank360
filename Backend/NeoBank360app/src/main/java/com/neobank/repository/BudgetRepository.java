package com.neobank.repository;
 
import com.neobank.entity.Budget;
import com.neobank.entity.BudgetCategory;
import com.neobank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
 
public interface BudgetRepository extends JpaRepository<Budget, Long> {
 
    Optional<Budget> findByUserAndCategoryAndBudgetMonth(
            User user,
            BudgetCategory category,
            LocalDate budgetMonth
    );
 
    List<Budget> findByUserAndBudgetMonthOrderByCategoryAsc(
            User user,
            LocalDate budgetMonth
    );
 
    List<Budget> findByUserOrderByBudgetMonthDescCategoryAsc(
            User user
    );
}