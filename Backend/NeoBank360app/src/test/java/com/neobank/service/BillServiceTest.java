
package com.neobank.service;

import com.neobank.dto.BillRequestDTO;
import com.neobank.dto.BillResponseDTO;
import com.neobank.dto.TransactionRequest;
import com.neobank.entity.*;
import com.neobank.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillService Tests")
class BillServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RewardService rewardService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BillService billService;

    private User testUser;
    private BillRequestDTO request;
    private Bill pendingBill;
    private Account account;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("customer@neobank.com");

        request = new BillRequestDTO();
        request.setBillerName("Electricity");
        request.setAmount(new BigDecimal("1500"));
        request.setDueDate(LocalDate.now().plusDays(5));
        request.setCategory("Electricity");

        pendingBill = new Bill();
        pendingBill.setId(10L);
        pendingBill.setUser(testUser);
        pendingBill.setAmount(new BigDecimal("1500"));
        pendingBill.setCategory("Electricity");
        pendingBill.setStatus(Bill.BillStatus.PENDING);

        account = new Account();
        account.setId(1L);
        account.setUser(testUser);
        account.setBalance(new BigDecimal("5000"));

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("customer@neobank.com");

        when(userRepository.findByEmail("customer@neobank.com"))
                .thenReturn(Optional.of(testUser));
    }

    /* ✅ CREATE BILL */
    @Test
    void testCreateBill() {

        when(billRepository.save(any())).thenReturn(pendingBill);

        BillResponseDTO response = billService.create(request);

        assertNotNull(response);
        assertEquals("PENDING", response.getStatus());

        verify(billRepository).save(any());
    }

    /* ✅ UPDATE STATUS → PAID */
    @Test
    void testUpdateStatusPaid() {

        when(billRepository.findById(10L))
                .thenReturn(Optional.of(pendingBill));

        when(accountRepository.findByUser(testUser))
                .thenReturn(List.of(account));

        when(billRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        BillResponseDTO response =
                billService.updateStatus(10L, "PAID");

        assertEquals("PAID", response.getStatus());

        // ✅ verify withdraw called
        verify(transactionService).withdraw(any(TransactionRequest.class));

        // ✅ verify reward (BigDecimal)
        verify(rewardService).addPoints(
                eq(testUser),
                eq(new BigDecimal("15.00"))
        );
    }

    /* ✅ INSUFFICIENT BALANCE */
    @Test
    void testInsufficientBalance() {

        account.setBalance(new BigDecimal("10"));

        when(billRepository.findById(10L))
                .thenReturn(Optional.of(pendingBill));

        when(accountRepository.findByUser(testUser))
                .thenReturn(List.of(account));

        assertThrows(ResponseStatusException.class,
                () -> billService.updateStatus(10L, "PAID"));
    }

    /* ✅ NON-PENDING BILL */
    @Test
    void testUpdateNonPending() {

        pendingBill.setStatus(Bill.BillStatus.PAID);

        when(billRepository.findById(10L))
                .thenReturn(Optional.of(pendingBill));

        assertThrows(ResponseStatusException.class,
                () -> billService.updateStatus(10L, "PAID"));
    }

    /* ✅ LIST BILLS */
    @Test
    void testListMine() {

        when(billRepository.findByUserOrderByDueDateAsc(testUser))
                .thenReturn(List.of(pendingBill));

        List<BillResponseDTO> list = billService.listMine();

        assertEquals(1, list.size());
    }

    /* ✅ GET BY ID SUCCESS */
    @Test
    void testGetById() {

        when(billRepository.findById(10L))
                .thenReturn(Optional.of(pendingBill));

        BillResponseDTO res = billService.getById(10L);

        assertNotNull(res);
    }

    /* ✅ FORBIDDEN ACCESS */
    @Test
    void testGetByIdForbidden() {

        User otherUser = new User();
        otherUser.setId(99L);

        pendingBill.setUser(otherUser);

        when(billRepository.findById(10L))
                .thenReturn(Optional.of(pendingBill));

        assertThrows(ResponseStatusException.class,
                () -> billService.getById(10L));
    }
}
