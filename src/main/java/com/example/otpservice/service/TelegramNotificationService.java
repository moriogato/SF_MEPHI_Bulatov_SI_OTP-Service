package com.example.otpservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class TelegramNotificationService {

    @Value("${telegram.bot.token:dummy_token}")
    private String botToken;

    @Value("${telegram.chat-id:dummy_chat_id}")
    private String chatId;

    @Value("${telegram.api-url:https://api.telegram.org/bot}")
    private String apiUrl;

    public void sendCode(String destination, String code) {
        try {
            String message = String.format("Hello %s, your verification code is: %s", destination, code);
            String url = String.format("%s%s/sendMessage?chat_id=%s&text=%s",
                    apiUrl,
                    botToken,
                    chatId,
                    URLEncoder.encode(message, StandardCharsets.UTF_8)
            );
            
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                log.info("Telegram message sent successfully to: {}", destination);
            } else {
                log.error("Telegram API error. Status code: {}", response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            log.error("Failed to send Telegram message: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }

    }
}