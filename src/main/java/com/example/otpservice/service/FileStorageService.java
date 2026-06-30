package com.example.otpservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class FileStorageService {

    private static final String OTP_DIRECTORY = "otp_codes";

    public void saveCode(String username, String code) {
        try {
            Path dir = Paths.get(OTP_DIRECTORY);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            // Используем безопасный формат без двоеточий
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String filename = String.format("%s_%s_%s.txt",
                    username,
                    timestamp,
                    code
            );

            Path file = dir.resolve(filename);
            String content = String.format("User: %s\nCode: %s\nTimestamp: %s\n",
                    username, code, LocalDateTime.now());

            Files.write(file, content.getBytes());
            log.info("OTP saved to file: {}", file);
        } catch (IOException e) {
            log.error("Failed to save OTP to file: {}", e.getMessage());
        }
    }
}