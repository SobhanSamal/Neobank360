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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BudgetService Tests")
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BudgetService budgetService;

    private User testUser;
    private BudgetRequestDTO request;
    private Budget groceriesBudget;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("customer@neobank.com");
        testUser.setFullName("Customer User");

        request = new BudgetRequestDTO();
        request.setCategory(BudgetCategory.GROCERIES);
        request.setBudgetMonth("2026-04");
        request.setLimitAmount(new BigDecimal("2000.00"));

        groceriesBudget = new Budget();
        groceriesBudget.setUser(testUser);
        groceriesBudget.setCategory(BudgetCategory.GROCERIES);
        groceriesBudget.setBudgetMonth(LocalDate.of(2026, 4, 1));
        groceriesBudget.setLimitAmount(new BigDecimal("2000.00"));
        groceriesBudget.setSpentAmount(BigDecimal.ZERO);
        groceriesBudget.setRemainingAmount(new BigDecimal("2000.00"));

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication())
                .thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("customer@neobank.com");

        when(userRepository.findByEmail("customer@neobank.com"))
                .thenReturn(Optional.of(testUser));
    }

    @Test
    @DisplayName("Should create budget for authenticated user")
    void testCreateBudgetSuccess() {

        when(budgetRepository.findByUserAndCategoryAndBudgetMonth(
                testUser,
                BudgetCategory.GROCERIES,
                LocalDate.of(2026, 4, 1)
        )).thenReturn(Optional.empty());

        when(budgetRepository.save(any(Budget.class)))
                .thenReturn(groceriesBudget);

        BudgetResponseDTO response =
                budgetService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("GROCERIES", response.getCategory());
        assertEquals("2026-04", response.getBudgetMonth());
        assertEquals(0,
                new BigDecimal("2000.00")
                        .compareTo(response.getLimitAmount()));

        verify(budgetRepository).save(argThat(budget ->
                budget.getUser().equals(testUser) &&
                budget.getCategory() == BudgetCategory.GROCERIES &&
                budget.getBudgetMonth()
                        .equals(LocalDate.of(2026, 4, 1))
        ));
    }

    @Test
    @DisplayName("Should reject duplicate budget for same user category and month")
    void testCreateBudgetDuplicate() {

        when(budgetRepository.findByUserAndCategoryAndBudgetMonth(
                testUser,
                BudgetCategory.GROCERIES,
                LocalDate.of(2026, 4, 1)
        )).thenReturn(Optional.of(groceriesBudget));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> budgetService.create(request));

        assertEquals(409, exception.getStatusCode().value());
        assertTrue(exception.getReason().contains("already exists"));

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    @DisplayName("Should reject malformed budget month")
    void testCreateBudgetInvalidMonth() {

        request.setBudgetMonth("04-2026");

        assertThrows(DateTimeParseException.class,
                () -> budgetService.create(request));

        verify(budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    @DisplayName("Should calculate budget summary utilization")
    void testGetSummaryCalculatesUtilization() {

        when(budgetRepository.findByUserAndBudgetMonthOrderByCategoryAsc(
                testUser,
                LocalDate.of(2026, 4, 1)
        )).thenReturn(List.of(groceriesBudget));

        when(transactionRepository.getTotalSpentByCategoryAndMonth(
                eq(testUser),
                eq("GROCERIES"),
                any(),
                any()
        )).thenReturn(new BigDecimal("400.00"));

        List<BudgetSummaryDTO> summary =
                budgetService.getSummary("2026-04");

        assertEquals(1, summary.size());

        BudgetSummaryDTO row = summary.get(0);

        assertEquals("GROCERIES", row.getCategory());
        assertEquals(0, new BigDecimal("2000.00")
                .compareTo(row.getLimitAmount()));
        assertEquals(0, new BigDecimal("400.00")
                .compareTo(row.getSpentAmount()));
        assertEquals(0, new BigDecimal("1600.00")
                .compareTo(row.getRemainingAmount()));
        assertEquals(0, new BigDecimal("20.00")
                .compareTo(row.getUtilizationPercentage()));
    }

    @Test
    @DisplayName("Should cap remaining amount at zero when spending exceeds limit")
    void testGetSummaryCapsRemainingAtZero() {

        when(budgetRepository.findByUserAndBudgetMonthOrderByCategoryAsc(
                testUser,
                LocalDate.of(2026, 4, 1)
        )).thenReturn(List.of(groceriesBudget));

        when(transactionRepository.getTotalSpentByCategoryAndMonth(
                eq(testUser),
                eq("GROCERIES"),
                any(),
                any()
        )).thenReturn(new BigDecimal("2500.00"));

        List<BudgetSummaryDTO> summary =
                budgetService.getSummary("2026-04");

        assertEquals(0,
                BigDecimal.ZERO.compareTo(
                        summary.get(0).getRemainingAmount()));

        assertEquals(0,
                new BigDecimal("125.00")
                        .compareTo(
                                summary.get(0)
                                        .getUtilizationPercentage()));
    }

    @Test
    @DisplayName("Should delete own budget")
    void testDeleteOwnBudget() {

        when(budgetRepository.findById(10L))
                .thenReturn(Optional.of(groceriesBudget));

        budgetService.delete(10L);

        verify(budgetRepository).delete(groceriesBudget);
    }

    @Test
    @DisplayName("Should reject deleting another user's budget")
    void testDeleteOtherUsersBudgetForbidden() {

        User otherUser = new User();
        otherUser.setId(2L);

        groceriesBudget.setUser(otherUser);

        when(budgetRepository.findById(10L))
                .thenReturn(Optional.of(groceriesBudget));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> budgetService.delete(10L));

        assertEquals(403, exception.getStatusCode().value());

        verify(budgetRepository, never())
                .delete(any(Budget.class));
    }
}