package com.example.otpservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpConfig {
    private Long id;
    private Integer ttlSeconds;
    private Integer codeLength;
    private LocalDateTime updatedAt;
}