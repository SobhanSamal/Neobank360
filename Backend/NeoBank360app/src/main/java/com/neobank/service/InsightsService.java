package com.neobank.service;

import com.neobank.dto.FinancialInsightsDTO;
import com.neobank.dto.TrendEntryDTO;
import com.neobank.repository.InsightsRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class InsightsService {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("MMM yyyy");

    private final InsightsRepository repo;

    public InsightsService(InsightsRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public FinancialInsightsDTO buildInsights(Long userId) {

        BigDecimal income = valueOrZero(repo.getTotalIncome(userId));
        BigDecimal expense = valueOrZero(repo.getTotalExpense(userId));

        BigDecimal savings = income.subtract(expense);

        return new FinancialInsightsDTO(
                income,
                expense,
                savings,
                buildTrend(userId)
        );
    }

    /* ✅ TREND LOGIC (6 MONTHS ✅) */
    private List<TrendEntryDTO> buildTrend(Long userId) {

        YearMonth current = YearMonth.now();
        YearMonth start = current.minusMonths(5);

        Map<YearMonth, TrendEntryDTO> map = new LinkedHashMap<>();

        // ✅ Pre-fill months
        for (int i = 0; i < 6; i++) {
            YearMonth m = start.plusMonths(i);
            map.put(m,
                new TrendEntryDTO(m.format(FORMAT),
                                  BigDecimal.ZERO,
                                  BigDecimal.ZERO));
        }

        List<Object[]> rows = repo.getMonthlyTrend(
                userId,
                start.atDay(1).atStartOfDay()
        );

        for (Object[] r : rows) {

            YearMonth m = YearMonth.of(toInt(r[0]), toInt(r[1]));

            TrendEntryDTO dto = map.get(m);

            if (dto != null) {
                dto.setTotalIncome(valueOrZero(toBigDecimal(r[2])));
                dto.setTotalExpense(valueOrZero(toBigDecimal(r[3])));
            }
        }

        return new ArrayList<>(map.values());
    }

    private BigDecimal valueOrZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal toBigDecimal(Object o) {
        if (o instanceof BigDecimal b) return b;
        return new BigDecimal(String.valueOf(o));
    }

    private int toInt(Object o) {
        return ((Number) o).intValue();
    }
}
