package com.neobank.repository;

import com.neobank.entity.Reward;
import com.neobank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    Optional<Reward> findByUser(User user);

    Optional<Reward> findByUserId(Long userId);
}
