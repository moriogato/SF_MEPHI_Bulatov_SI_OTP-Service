package com.example.otpservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpGenerateRequest {
    @NotBlank(message = "Operation ID is required")
    private String operationId;

    @NotBlank(message = "Delivery channel is required")
    private String deliveryChannel; // email, sms, telegram, file
}