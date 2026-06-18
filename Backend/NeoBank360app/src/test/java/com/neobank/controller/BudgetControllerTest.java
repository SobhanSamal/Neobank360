package com.neobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.neobank.dto.BudgetRequestDTO;
import com.neobank.dto.BudgetResponseDTO;
import com.neobank.dto.BudgetSummaryDTO;

import com.neobank.entity.BudgetCategory;
import com.neobank.service.BudgetService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("BudgetController Tests")
class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private BudgetController budgetController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(budgetController)
                .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should create budget and return 201")
    void testCreateBudget() throws Exception {

        BudgetRequestDTO request = new BudgetRequestDTO();
        request.setCategory(BudgetCategory.GROCERIES);
        request.setBudgetMonth("2026-04");
        request.setLimitAmount(new BigDecimal("2000.00"));

        BudgetResponseDTO response = new BudgetResponseDTO();
        response.setId(1L);
        response.setUserId(10L);
        response.setCategory("GROCERIES");
        response.setBudgetMonth("2026-04");
        response.setLimitAmount(new BigDecimal("2000.00"));

        when(budgetService.create(any(BudgetRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.userId", is(10)))
            .andExpect(jsonPath("$.category", is("GROCERIES")))
            .andExpect(jsonPath("$.budgetMonth", is("2026-04")))
            .andExpect(jsonPath("$.limitAmount",
                    comparesEqualTo(2000.00)));

        verify(budgetService).create(any(BudgetRequestDTO.class));
    }

    @Test
    @DisplayName("Should reject invalid budget creation request")
    void testCreateBudgetValidationFailure() throws Exception {

        BudgetRequestDTO request = new BudgetRequestDTO();
        request.setCategory(BudgetCategory.GROCERIES);
        request.setBudgetMonth("");
        request.setLimitAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(budgetService, never()).create(any());
    }

    @Test
    @DisplayName("Should fetch monthly budget summary")
    void testGetSummary() throws Exception {

        BudgetSummaryDTO summary = new BudgetSummaryDTO(
                "GROCERIES",
                "2026-04",
                new BigDecimal("2000.00"),
                new BigDecimal("400.00"),
                new BigDecimal("1600.00"),
                new BigDecimal("20.00")
        );

        when(budgetService.getSummary("2026-04"))
                .thenReturn(List.of(summary));

        mockMvc.perform(get("/api/budgets/summary/2026-04"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].category", is("GROCERIES")))
            .andExpect(jsonPath("$[0].budgetMonth", is("2026-04")))
            .andExpect(jsonPath("$[0].limitAmount",
                    comparesEqualTo(2000.00)))
            .andExpect(jsonPath("$[0].spentAmount",
                    comparesEqualTo(400.00)))
            .andExpect(jsonPath("$[0].remainingAmount",
                    comparesEqualTo(1600.00)))
            .andExpect(jsonPath("$[0].utilizationPercentage",
                    comparesEqualTo(20.00)));

        verify(budgetService).getSummary("2026-04");
    }

    @Test
    @DisplayName("Should list authenticated user's budgets")
    void testListMine() throws Exception {

        BudgetResponseDTO response = new BudgetResponseDTO();
        response.setId(1L);
        response.setUserId(10L);
        response.setCategory("RENT");
        response.setBudgetMonth("2026-04");
        response.setLimitAmount(new BigDecimal("12000.00"));

        when(budgetService.listMine())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/budgets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].category", is("RENT")));

        verify(budgetService).listMine();
    }

    @Test
    @DisplayName("Should delete budget and return 204")
    void testDeleteBudget() throws Exception {

        mockMvc.perform(delete("/api/budgets/1"))
            .andExpect(status().isNoContent());

        verify(budgetService).delete(1L);
    }
}
