package com.neobank.config;

import com.neobank.entity.User;
import com.neobank.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "admin@neobank.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_FULL_NAME = "Admin User";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        User admin = new User();
        admin.setEmail(ADMIN_EMAIL);
        admin.setFullName(ADMIN_FULL_NAME);
        admin.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(User.Role.ADMIN);
        admin.setIsActive(true);

        userRepository.save(admin);
    }
}
