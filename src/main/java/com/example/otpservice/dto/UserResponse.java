package com.example.otpservice.dto;

import com.example.otpservice.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String role;
    private String email;
    private String phoneNumber;
    private String telegramId;
    private LocalDateTime createdAt;

    public static UserResponse fromUser(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setRole(user.getRole());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setTelegramId(user.getTelegramId());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}