package com.neobank.controller;
 
import com.neobank.dto.RewardDTO;
import com.neobank.dto.RewardHistoryDTO;
import com.neobank.service.RewardService;
 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/rewards")
public class RewardController {
 
    private final RewardService rewardService;
 
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }
 
    // JWT-based reward fetch
    @GetMapping
    public ResponseEntity<RewardDTO> getBalance() {
        return ResponseEntity.ok(
                rewardService.getBalance()
        );
    }
 
    @GetMapping("/history")
    public ResponseEntity<List<RewardHistoryDTO>> getHistory() {
        return ResponseEntity.ok(rewardService.getHistory());
    }
}
 