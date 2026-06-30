package com.example.otpservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpValidateRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Operation ID is required")
    private String operationId;
}