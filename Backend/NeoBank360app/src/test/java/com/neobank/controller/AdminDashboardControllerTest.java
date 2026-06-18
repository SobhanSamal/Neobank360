package com.neobank.controller;
 
import com.neobank.dto.FinancialInsightsDTO;
import com.neobank.service.CurrentUserService;
import com.neobank.service.InsightsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
 
import java.math.BigDecimal;
import java.util.List;
 
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 
@ExtendWith(MockitoExtension.class)
@DisplayName("InsightsController Tests")
class InsightsControllerTest {
 
    @Mock
    private InsightsService insightsService;
 
    @Mock
    private CurrentUserService currentUserService;
 
    @InjectMocks
    private InsightsController controller;
 
    @Test
    @DisplayName("Should return insights for owned user")
    void getInsightsReturnsData() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
 
        FinancialInsightsDTO dto = new FinancialInsightsDTO(
                new BigDecimal("3000.00"),
                new BigDecimal("1200.00"),
                new BigDecimal("1800.00"),
                List.of()
        );
 
        doNothing().when(currentUserService).assertOwner(1L);
        when(insightsService.buildInsights(anyLong())).thenReturn(dto);
 
        mockMvc.perform(get("/api/insights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome", is(3000.00)))
                .andExpect(jsonPath("$.totalExpense", is(1200.00)))
                .andExpect(jsonPath("$.savings", is(1800.00)));
    }
}
 