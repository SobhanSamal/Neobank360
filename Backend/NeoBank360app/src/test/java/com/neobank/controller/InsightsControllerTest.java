package com.neobank.controller;
 
import com.neobank.dto.AdminDashboardDTO;
import com.neobank.dto.PendingApprovalDTO;
import com.neobank.dto.SystemHealthDTO;
import com.neobank.service.AdminDashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
 
import java.util.List;
 
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardController Tests")
class AdminDashboardControllerTest {
 
    @Mock
    private AdminDashboardService service;
 
    @InjectMocks
    private AdminDashboardController controller;
 
    @Test
    @DisplayName("Should return admin dashboard metrics")
    void dashboardEndpointWorks() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        when(service.buildDashboard()).thenReturn(new AdminDashboardDTO(10, 8, 4, 12, 3, 50, 0.0));
 
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers", is(10)))
                .andExpect(jsonPath("$.pendingApprovals", is(3)));
    }
 
    @Test
    @DisplayName("Should return pending approvals list")
    void pendingApprovalsEndpointWorks() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        when(service.getPendingApprovals()).thenReturn(List.of(
                new PendingApprovalDTO(1L, "LOAN_APPLICATION", "Applicant", "Product", 1000.0, java.time.LocalDateTime.now())
        ));
 
        mockMvc.perform(get("/api/admin/pending-approvals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].applicantName", is("Applicant")));
    }
 

}
 