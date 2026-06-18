package com.neobank.controller;

import com.neobank.dto.RewardDTO;
import com.neobank.service.RewardService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;   // ✅ IMPORTANT
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RewardController Tests")
class RewardControllerTest {

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private RewardController rewardController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(rewardController)
                .build();
    }

    @Test
    @DisplayName("Should fetch reward balance")
    void testGetBalance() throws Exception {

        RewardDTO response = new RewardDTO();
        response.setUserId(10L);

        // ✅ FIX: use BigDecimal
        response.setPointsBalance(new BigDecimal("250.50"));

        response.setLastUpdated(LocalDateTime.now());

        when(rewardService.getBalance()).thenReturn(response);

        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isOk())

                // ✅ userId
                .andExpect(jsonPath("$.userId", is(10)))

                // ✅ points (decimal)
                .andExpect(jsonPath("$.pointsBalance", is(250.50)));

        verify(rewardService).getBalance();
    }
}

