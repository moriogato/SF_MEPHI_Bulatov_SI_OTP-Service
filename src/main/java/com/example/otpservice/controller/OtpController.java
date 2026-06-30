package com.example.otpservice.controller;

import com.example.otpservice.dto.OtpGenerateRequest;
import com.example.otpservice.dto.OtpValidateRequest;
import com.example.otpservice.entity.OtpCode;
import com.example.otpservice.entity.User;
import com.example.otpservice.service.OtpService;
import com.example.otpservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/otp")
@Slf4j
public class OtpController {

    private final OtpService otpService;
    private final UserService userService;

    public OtpController(OtpService otpService, UserService userService) {
        this.otpService = otpService;
        this.userService = userService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateOtp(
            @Valid @RequestBody OtpGenerateRequest request,
            Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OtpCode otpCode = otpService.generateOtp(
                user,
                request.getOperationId(),
                request.getDeliveryChannel()
        );

        return ResponseEntity.ok("OTP generated and sent via " + request.getDeliveryChannel());
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateOtp(@Valid @RequestBody OtpValidateRequest request) {
        boolean isValid = otpService.validateOtp(request.getCode(), request.getOperationId());

        if (isValid) {
            return ResponseEntity.ok("OTP validated successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }
}