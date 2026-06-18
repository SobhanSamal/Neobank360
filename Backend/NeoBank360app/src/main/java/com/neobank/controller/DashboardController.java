package com.neobank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @GetMapping("/customer/dashboard")
    public ResponseEntity<Map<String, String>> customerDashboard() {
        return ResponseEntity.ok(Map.of(
            "message", "Customer dashboard data (Day 3 protected route)",
            "status", "OK"
        ));
    }


}
