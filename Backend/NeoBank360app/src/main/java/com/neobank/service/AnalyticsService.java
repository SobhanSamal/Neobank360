package com.neobank.service;
 
import com.neobank.dto.AdminLoanAnalyticsDTO;
import com.neobank.dto.AdminTransactionAnalyticsDTO;
import com.neobank.dto.DailyVolumeDTO;
import com.neobank.dto.SystemAuditLogDTO;
import com.neobank.dto.UserSpendingAnalyticsDTO;
import com.neobank.dto.UserWealthAnalyticsDTO;
import com.neobank.entity.Account;
import com.neobank.entity.Budget;
import com.neobank.entity.BudgetCategory;
import com.neobank.entity.LoanAccount;
import com.neobank.entity.LoanApplication;
import com.neobank.entity.LoanRepayment;
import com.neobank.entity.Reward;
import com.neobank.entity.SystemAuditLog;
import com.neobank.entity.Transaction;
import com.neobank.entity.User;
import com.neobank.repository.AccountRepository;
import com.neobank.repository.BudgetRepository;
import com.neobank.repository.LoanAccountRepository;
import com.neobank.repository.LoanApplicationRepository;
import com.neobank.repository.LoanRepaymentRepository;
import com.neobank.repository.RewardRepository;
import com.neobank.repository.SystemAuditLogRepository;
import com.neobank.repository.TransactionRepository;
 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
@Service
public class AnalyticsService {
    private final TransactionRepository transactionRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final LoanRepaymentRepository loanRepaymentRepository;
    private final BudgetRepository budgetRepository;
    private final AccountRepository accountRepository;
    private final RewardRepository rewardRepository;
    private final SystemAuditLogRepository systemAuditLogRepository;
    private final CurrentUserService currentUserService;
 
    public AnalyticsService(TransactionRepository transactionRepository,
                            LoanApplicationRepository loanApplicationRepository,
                            LoanAccountRepository loanAccountRepository,
                            LoanRepaymentRepository loanRepaymentRepository,
                            BudgetRepository budgetRepository,
                            AccountRepository accountRepository,
                            RewardRepository rewardRepository,
                            SystemAuditLogRepository systemAuditLogRepository,
                            CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanAccountRepository = loanAccountRepository;
        this.loanRepaymentRepository = loanRepaymentRepository;
        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
        this.rewardRepository = rewardRepository;
        this.systemAuditLogRepository = systemAuditLogRepository;
        this.currentUserService = currentUserService;
    }
 
    @Transactional(readOnly = true)
    public AdminTransactionAnalyticsDTO getAdminTransactionAnalytics(String timeframe) {
        LocalDateTime[] range = resolveRange(timeframe);
        List<Transaction> txns = transactionRepository.findByTransactionDateBetween(range[0], range[1]);
        Map<LocalDate, List<Transaction>> grouped = txns.stream().collect(Collectors.groupingBy(t -> t.getTransactionDate().toLocalDate(), LinkedHashMap::new, Collectors.toList()));
 
        List<DailyVolumeDTO> daily = new ArrayList<>();
        BigDecimal inflow = BigDecimal.ZERO;
        BigDecimal outflow = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
        long count = 0;
        for (Map.Entry<LocalDate, List<Transaction>> e : grouped.entrySet()) {
            BigDecimal sum = e.getValue().stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            long txnCount = e.getValue().size();
            daily.add(new DailyVolumeDTO(e.getKey(), sum, txnCount));
            total = total.add(sum);
            count += txnCount;
            for (Transaction t : e.getValue()) {
                if (t.getType() == Transaction.TransactionType.CREDIT) inflow = inflow.add(t.getAmount());
                if (t.getType() == Transaction.TransactionType.DEBIT) outflow = outflow.add(t.getAmount());
            }
        }
        AdminTransactionAnalyticsDTO dto = new AdminTransactionAnalyticsDTO();
        dto.setDailyVolumes(daily);
        dto.setTotalInflow(inflow);
        dto.setTotalOutflow(outflow);
        dto.setAverageTicketSize(count == 0 ? BigDecimal.ZERO : total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
        return dto;
    }
 
    @Transactional(readOnly = true)
    public AdminLoanAnalyticsDTO getAdminLoanAnalytics() {
        List<LoanApplication> apps = loanApplicationRepository.findAll();
        Map<String, Long> dist = apps.stream().collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));
        long npaCount = loanRepaymentRepository.findAll().stream()
                .filter(r -> r.getPaymentStatus() == LoanRepayment.Status.OVERDUE)
                .count();
        long totalLoans = loanAccountRepository.findAll().size();
        AdminLoanAnalyticsDTO dto = new AdminLoanAnalyticsDTO();
        dto.setLoanDistribution(dist);
        dto.setNpaCount(npaCount);
        dto.setNpaRatio(totalLoans == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(npaCount * 100.0 / totalLoans).setScale(2, RoundingMode.HALF_UP));
        return dto;
    }
 
    @Transactional(readOnly = true)
    public Page<SystemAuditLogDTO> getSystemLogs(LocalDateTime from, LocalDateTime to, Integer status, int page, int size) {
        Page<SystemAuditLog> logs = systemAuditLogRepository
                .findByEventTimestampBetweenAndResponseStatusGreaterThanEqualOrderByEventTimestampDesc(
                        from, to, status == null ? 0 : status, PageRequest.of(page, size));
        return logs.map(this::map);
    }
 
    @Transactional(readOnly = true)
    public UserSpendingAnalyticsDTO getUserSpendingAnalytics(Long userId, int months) {
        currentUserService.assertOwner(userId);
        User currentUser = currentUserService.getCurrentUser();
        YearMonth end = YearMonth.now();
        YearMonth start = end.minusMonths(Math.max(0, months - 1));
        List<UserSpendingAnalyticsDTO.CategorySpendDTO> categorySpending = new ArrayList<>();
        List<UserSpendingAnalyticsDTO.BudgetVsSpendDTO> budgetVsActual = new ArrayList<>();
        for (BudgetCategory category : BudgetCategory.values()) {
            BigDecimal spent = transactionRepository.getTotalSpentByCategoryAndMonth(currentUser, category.name(), start.atDay(1).atStartOfDay(), end.plusMonths(1).atDay(1).atStartOfDay());
            categorySpending.add(new UserSpendingAnalyticsDTO.CategorySpendDTO(category.name(), spent));
            Budget budget = budgetRepository.findByUserAndCategoryAndBudgetMonth(currentUser, category, end.atDay(1)).orElse(null);
            budgetVsActual.add(new UserSpendingAnalyticsDTO.BudgetVsSpendDTO(category.name(), budget == null ? BigDecimal.ZERO : budget.getLimitAmount(), spent));
        }
        UserSpendingAnalyticsDTO dto = new UserSpendingAnalyticsDTO();
        dto.setCategorySpending(categorySpending);
        dto.setBudgetVsActual(budgetVsActual);
        return dto;
    }
 
    @Transactional(readOnly = true)
    public UserWealthAnalyticsDTO getUserWealthAnalytics(Long userId) {
        currentUserService.assertOwner(userId);
        User currentUser = currentUserService.getCurrentUser();
        List<Account> accounts = accountRepository.findByUser(currentUser);
        List<LoanAccount> loans = loanAccountRepository.findByUserId(userId);
        List<Reward> reward = rewardRepository.findByUserId(userId).map(List::of).orElseGet(List::of);
        BigDecimal totalBalance = accounts.stream().map(a -> a.getBalance()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = loans.stream().map(l -> BigDecimal.valueOf(l.getPrincipalAmount() == null ? 0.0 : l.getPrincipalAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        UserWealthAnalyticsDTO dto = new UserWealthAnalyticsDTO();
        dto.setNetWorthTimeline(List.of(new UserWealthAnalyticsDTO.NetWorthPointDTO(LocalDate.now().withDayOfMonth(1), totalBalance, outstanding, totalBalance.subtract(outstanding))));
        dto.setLoanPayoffForecast(loans.stream().map(l -> new UserWealthAnalyticsDTO.LoanPayoffForecastDTO(l.getId(), l.getTenureMonths(), LocalDate.now().plusMonths(l.getTenureMonths() == null ? 0 : l.getTenureMonths()))).toList());
        dto.setRewardAccrualHistory(List.of(new UserWealthAnalyticsDTO.RewardAccrualDTO(LocalDate.now().withDayOfMonth(1), reward.stream().findFirst().map(r -> r.getPointsBalance()).orElse(BigDecimal.ZERO))));
        return dto;
    }
 
    private SystemAuditLogDTO map(SystemAuditLog log) {
        SystemAuditLogDTO dto = new SystemAuditLogDTO();
        dto.setId(log.getId());
        dto.setEndpoint(log.getEndpoint());
        dto.setHttpMethod(log.getHttpMethod());
        dto.setResponseStatus(log.getResponseStatus());
        dto.setExecutionTimeMs(log.getExecutionTimeMs());
        dto.setActingUserId(log.getActingUserId());
        dto.setEventTimestamp(log.getEventTimestamp());
        dto.setErrorMessage(log.getErrorMessage());
        return dto;
    }
 
    private LocalDateTime[] resolveRange(String timeframe) {
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        LocalDateTime start = switch (timeframe == null ? "" : timeframe.toLowerCase()) {
            case "7d" -> end.minusDays(7);
            case "30d" -> end.minusDays(30);
            case "ytd" -> LocalDate.now().withDayOfYear(1).atStartOfDay();
            default -> throw new IllegalArgumentException("Invalid timeframe");
        };
        return new LocalDateTime[]{start, end};
    }
}

 