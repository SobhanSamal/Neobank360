package com.neobank.service;
 
import com.neobank.dto.RewardDTO;
import com.neobank.dto.RewardHistoryDTO;
import com.neobank.entity.Bill;
import com.neobank.entity.Reward;
import com.neobank.entity.User;
import com.neobank.repository.BillRepository;
import com.neobank.repository.RewardRepository;
import com.neobank.repository.UserRepository;
 
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
 
import java.math.BigDecimal;
 
@Service
public class RewardService {
 
    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;
    private final BillRepository billRepository;
 
    public RewardService(
            RewardRepository rewardRepository,
            UserRepository userRepository,
            BillRepository billRepository
    ) {
        this.rewardRepository = rewardRepository;
        this.userRepository = userRepository;
        this.billRepository = billRepository;
    }
 
    /* =========================
       GET BALANCE
    ========================= */
    @Transactional
    public RewardDTO getBalance() {
        User user = getAuthenticatedUser();
        return RewardDTO.from(getOrCreateReward(user));
    }
 
    @Transactional(readOnly = true)
    public java.util.List<RewardHistoryDTO> getHistory() {
        User user = getAuthenticatedUser();
        return billRepository.findByUserAndStatusOrderByPaidAtDesc(user, Bill.BillStatus.PAID)
                .stream()
                .map(RewardHistoryDTO::from)
                .toList();
    }
 
    /* =========================
       ADD POINTS ✅ FIXED
    ========================= */
    @Transactional
    public RewardDTO addPoints(User user, BigDecimal points) {
 
        if (points == null || points.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "points must be greater than zero"
            );
        }
 
        Reward reward = getOrCreateReward(user);
 
        BigDecimal currentBalance = reward.getPointsBalance() != null
                ? reward.getPointsBalance()
                : BigDecimal.ZERO;
 
        reward.setPointsBalance(currentBalance.add(points));
 
        return RewardDTO.from(
                rewardRepository.save(reward)
        );
    }
 
    /* =========================
       DEDUCT POINTS ✅ FIXED
    ========================= */
    @Transactional
    public RewardDTO deductPoints(BigDecimal points) {
 
        User user = getAuthenticatedUser();
 
        if (points == null || points.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "points must be greater than zero"
            );
        }
 
        Reward reward = getOrCreateReward(user);
 
        if (reward.getPointsBalance().compareTo(points) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Reward balance cannot become negative"
            );
        }
 
        reward.setPointsBalance(
                reward.getPointsBalance().subtract(points)
        );
 
        return RewardDTO.from(
                rewardRepository.save(reward)
        );
    }
 
    /* =========================
       CREATE IF NOT EXISTS
    ========================= */
    private Reward getOrCreateReward(User user) {
        return rewardRepository
                .findByUser(user)
                .orElseGet(() -> {
                    Reward reward = new Reward();
                    reward.setUser(user);
                    reward.setPointsBalance(BigDecimal.ZERO);
                    return rewardRepository.save(reward);
                });
    }
 
    /* =========================
       AUTH USER
    ========================= */
    private User getAuthenticatedUser() {
 
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
 
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized"
            );
        }
 
        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Authenticated user not found"
                        )
                );
    }
}
 