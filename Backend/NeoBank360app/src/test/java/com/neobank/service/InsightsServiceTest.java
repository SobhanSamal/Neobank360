package com.neobank.service;
 
import com.neobank.dto.FinancialInsightsDTO;
import com.neobank.dto.TrendEntryDTO;
import com.neobank.repository.InsightsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
 
@ExtendWith(MockitoExtension.class)
@DisplayName("InsightsService Tests")
class InsightsServiceTest {
 
    @Mock
    private InsightsRepository repo;
 
    @InjectMocks
    private InsightsService insightsService;
 
    @Test
    @DisplayName("buildInsights computes savings and pads trend to 6 months")
    void buildInsightsWorks() {
        when(repo.getTotalIncome(anyLong())).thenReturn(new BigDecimal("5000.00"));
        when(repo.getTotalExpense(anyLong())).thenReturn(new BigDecimal("1250.00"));
        when(repo.getMonthlyTrend(anyLong(), any(LocalDateTime.class))).thenReturn(List.of());
 
        FinancialInsightsDTO dto = insightsService.buildInsights(1L);
 
        assertNotNull(dto);
        assertEquals(0, new BigDecimal("5000.00").compareTo(dto.getTotalIncome()));
        assertEquals(0, new BigDecimal("1250.00").compareTo(dto.getTotalExpense()));
        assertEquals(0, new BigDecimal("3750.00").compareTo(dto.getSavings()));
        assertEquals(6, dto.getTrendSummary().size());
        for (TrendEntryDTO trend : dto.getTrendSummary()) {
            assertEquals(0, BigDecimal.ZERO.compareTo(trend.getTotalIncome()));
            assertEquals(0, BigDecimal.ZERO.compareTo(trend.getTotalExpense()));
        }
    }
}
 