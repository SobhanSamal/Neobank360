package com.neobank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.neobank.dto.BillRequestDTO;
import com.neobank.dto.BillResponseDTO;
import com.neobank.service.BillService;

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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillController Tests")
class BillControllerTest {

    @Mock
    private BillService billService;

    @InjectMocks
    private BillController billController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(billController)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("Should create bill and return 201")
    void testCreateBill() throws Exception {

        BillRequestDTO request = new BillRequestDTO();
        request.setBillerName("Broadband");
        request.setAmount(new BigDecimal("999.00"));
        request.setDueDate(LocalDate.now().plusDays(10));

        BillResponseDTO response =
                billResponse("Broadband", "PENDING", false);

        when(billService.create(any(BillRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/bills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.userId", is(10)))
            .andExpect(jsonPath("$.billerName", is("Broadband")))
            .andExpect(jsonPath("$.amount",
                    comparesEqualTo(999.00)))
            .andExpect(jsonPath("$.status", is("PENDING")))
            .andExpect(jsonPath("$.remindMe", is(false)));

        verify(billService).create(any(BillRequestDTO.class));
    }

    @Test
    @DisplayName("Should reject invalid bill creation request")
    void testCreateBillValidationFailure() throws Exception {

        BillRequestDTO request = new BillRequestDTO();
        request.setBillerName("");
        request.setAmount(BigDecimal.ZERO);
        request.setDueDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/api/bills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());

        verify(billService, never()).create(any());
    }

    @Test
    @DisplayName("Should list bills for authenticated user")
    void testListMine() throws Exception {

        BillResponseDTO response =
                billResponse("Electricity", "PENDING", true);

        when(billService.listMine()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/bills"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].billerName",
                    is("Electricity")))
            .andExpect(jsonPath("$[0].remindMe", is(true)));

        verify(billService).listMine();
    }

    @Test
    @DisplayName("Should fetch bill by id")
    void testGetById() throws Exception {

        when(billService.getById(1L))
                .thenReturn(
                        billResponse("Rent", "PENDING", false)
                );

        mockMvc.perform(get("/api/bills/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.billerName", is("Rent")));

        verify(billService).getById(1L);
    }

    @Test
    @DisplayName("Should update bill status")
    void testUpdateStatus() throws Exception {

        BillResponseDTO response =
                billResponse("Broadband", "PAID", false);

        when(billService.updateStatus(1L, "PAID"))
                .thenReturn(response);

        mockMvc.perform(patch("/api/bills/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        Map.of("status", "PAID")
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("PAID")));

        verify(billService).updateStatus(1L, "PAID");
    }

    private BillResponseDTO billResponse(
            String billerName,
            String status,
            boolean remindMe
    ) {
        BillResponseDTO response = new BillResponseDTO();

        response.setId(1L);
        response.setUserId(10L);
        response.setBillerName(billerName);
        response.setAmount(new BigDecimal("999.00"));
        response.setDueDate(LocalDate.now().plusDays(2));
        response.setStatus(status);
        response.setRemindMe(remindMe);

        return response;
    }
}