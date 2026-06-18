package com.neobank.service;

import com.neobank.dto.RewardDTO;
import com.neobank.entity.Reward;
import com.neobank.entity.User;
import com.neobank.repository.RewardRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RewardService Tests")
class RewardServiceTest {

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private RewardService rewardService;

    private User testUser;
    private Reward reward;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("customer@neobank.com");

        reward = new Reward();
        reward.setId(1L);
        reward.setUser(testUser);

        // ✅ FIX: BigDecimal
        reward.setPointsBalance(new BigDecimal("100.00"));

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("customer@neobank.com");

        when(userRepository.findByEmail("customer@neobank.com"))
                .thenReturn(Optional.of(testUser));
    }

    /* =========================
       GET BALANCE
    ========================= */
    @Test
    void testGetBalance() {

        when(rewardRepository.findByUser(testUser))
                .thenReturn(Optional.of(reward));

        RewardDTO response = rewardService.getBalance();

        assertEquals(new BigDecimal("100.00"), response.getPointsBalance());
        verify(rewardRepository, never()).save(any());
    }

    /* =========================
       CREATE NEW REWARD
    ========================= */
    @Test
    void testCreateNewReward() {

        when(rewardRepository.findByUser(testUser))
                .thenReturn(Optional.empty());

        when(rewardRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        RewardDTO response = rewardService.getBalance();

        assertEquals(BigDecimal.ZERO, response.getPointsBalance());

        verify(rewardRepository).save(any());
    }

    /* =========================
       ADD POINTS
    ========================= */
    @Test
    void testAddPoints() {

        when(rewardRepository.findByUser(testUser))
                .thenReturn(Optional.of(reward));

        when(rewardRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        RewardDTO response =
                rewardService.addPoints(
                        testUser,
                        new BigDecimal("25.50")
                );

        assertEquals(new BigDecimal("125.50"), response.getPointsBalance());

        verify(rewardRepository).save(reward);
    }

    /* =========================
       INVALID ADD
    ========================= */
    @Test
    void testAddPointsInvalid() {

        assertThrows(ResponseStatusException.class,
                () -> rewardService.addPoints(
                        testUser,
                        BigDecimal.ZERO
                ));

        verify(rewardRepository, never()).save(any());
    }

    /* =========================
       DEDUCT POINTS
    ========================= */
    @Test
    void testDeductPoints() {

        when(rewardRepository.findByUser(testUser))
                .thenReturn(Optional.of(reward));

        when(rewardRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        RewardDTO response =
                rewardService.deductPoints(
                        new BigDecimal("40.00")
                );

        assertEquals(new BigDecimal("60.00"), response.getPointsBalance());

        verify(rewardRepository).save(reward);
    }

    /* =========================
       NEGATIVE BALANCE
    ========================= */
    @Test
    void testDeductPointsFails() {

        when(rewardRepository.findByUser(testUser))
                .thenReturn(Optional.of(reward));

        assertThrows(ResponseStatusException.class,
                () -> rewardService.deductPoints(
                        new BigDecimal("200.00")
                ));

        verify(rewardRepository, never()).save(any());
    }

    /* =========================
       INVALID DEDUCT
    ========================= */
    @Test
    void testDeductPointsInvalid() {

        assertThrows(ResponseStatusException.class,
                () -> rewardService.deductPoints(BigDecimal.ZERO));

        verify(rewardRepository, never()).save(any());
    }
}
