package com.example.otpservice.util;

import java.security.SecureRandom;

public class OTPGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateNumericCode(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Code length must be at least 1");
        }

        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = SECURE_RANDOM.nextInt(10);
            code.append(digit);
        }
        return code.toString();
    }

    public static String generateAlphanumericCode(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Code length must be at least 1");
        }

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        return code.toString();
    }
}