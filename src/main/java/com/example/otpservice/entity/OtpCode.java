package com.example.otpservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpCode {
    private Long id;
    private String code;
    private String operationId;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private OtpStatus status;
    private String deliveryChannel;
}