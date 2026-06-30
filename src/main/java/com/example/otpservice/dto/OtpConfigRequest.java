package com.example.otpservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OtpConfigRequest {
    @NotNull(message = "TTL is required")
    @Min(value = 60, message = "TTL must be at least 60 seconds")
    private Integer ttlSeconds;

    @NotNull(message = "Code length is required")
    @Min(value = 4, message = "Code length must be at least 4")
    private Integer codeLength;
}