package com.example.otpservice.exception;

public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }

    public InvalidOtpException(String code, String operationId) {
        super(String.format("Invalid OTP: %s for operation: %s", code, operationId));
    }
}