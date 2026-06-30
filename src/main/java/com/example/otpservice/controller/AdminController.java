package com.example.otpservice.controller;

import com.example.otpservice.dto.OtpConfigRequest;
import com.example.otpservice.dto.UserResponse;
import com.example.otpservice.entity.OtpConfig;
import com.example.otpservice.entity.User;
import com.example.otpservice.service.OtpService;
import com.example.otpservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminController {

    private final UserService userService;
    private final OtpService otpService;

    public AdminController(UserService userService, OtpService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllNonAdminUsers();
        List<UserResponse> responses = users.stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/otp-config")
    public ResponseEntity<OtpConfig> updateOtpConfig(@Valid @RequestBody OtpConfigRequest request) {
        OtpConfig config = otpService.updateOtpConfig(
                request.getTtlSeconds(),
                request.getCodeLength()
        );
        return ResponseEntity.ok(config);
    }

    @GetMapping("/otp-config")
    public ResponseEntity<OtpConfig> getOtpConfig() {
        return otpService.getOtpConfig()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}