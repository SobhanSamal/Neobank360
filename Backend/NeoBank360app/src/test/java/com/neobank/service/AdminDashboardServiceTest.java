package com.neobank.service;
 
import com.neobank.dto.AdminDashboardDTO;
import com.neobank.dto.PendingApprovalDTO;
import com.neobank.dto.SystemHealthDTO;
import com.neobank.repository.AdminDashboardRepository;
import com.neobank.repository.PendingApprovalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
 
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardService Tests")
class AdminDashboardServiceTest {
 
    @Mock
    private AdminDashboardRepository repo;
 
    @Mock
    private PendingApprovalRepository pendingRepo;
 
    @Mock
    private DataSource dataSource;
 
    @Mock
    private Connection connection;
 
    @Mock
    private PreparedStatement preparedStatement;
 
    @InjectMocks
    private AdminDashboardService service;
 
    @Test
    @DisplayName("buildDashboard returns counts and zero platform savings rate")
    void buildDashboardReturnsCounts() {
        when(repo.totalUsers()).thenReturn(10L);
        when(repo.totalActiveUsers()).thenReturn(8L);
        when(repo.totalLoans()).thenReturn(4L);
        when(repo.totalAccounts()).thenReturn(12L);
        when(repo.pendingApprovals()).thenReturn(3L);
        when(repo.totalTransactions()).thenReturn(50L);
 
        AdminDashboardDTO dto = service.buildDashboard();
 
        assertEquals(10L, dto.getTotalUsers());
        assertEquals(8L, dto.getTotalActiveUsers());
        assertEquals(4L, dto.getTotalLoans());
        assertEquals(12L, dto.getTotalAccounts());
        assertEquals(3L, dto.getPendingApprovals());
        assertEquals(50L, dto.getTotalTransactions());
        assertEquals(0.0, dto.getPlatformSavingsRate());
    }
 
    @Test
    @DisplayName("getPendingApprovals returns repository rows")
    void getPendingApprovalsReturnsRows() {
        PendingApprovalDTO dto = new PendingApprovalDTO(1L, "LOAN_APPLICATION", "Applicant", "Product", 1000.0, java.time.LocalDateTime.now());
        when(pendingRepo.findPendingApprovals()).thenReturn(List.of(dto));
 
        List<PendingApprovalDTO> result = service.getPendingApprovals();
 
        assertEquals(1, result.size());
        assertEquals("Applicant", result.get(0).getApplicantName());
    }
 
    @Test
    @DisplayName("getSystemHealth returns UP when DB responds")
    void getSystemHealthReturnsUp() throws Exception {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement("SELECT 1")).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true);
 
        SystemHealthDTO dto = service.getSystemHealth();
 
        assertEquals("UP", dto.getDbStatus());
        assertEquals(0, dto.getActiveSessions());
    }
}
 