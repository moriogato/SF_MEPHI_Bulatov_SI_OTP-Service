package com.example.otpservice.service;

import com.example.otpservice.entity.*;
import com.example.otpservice.repository.OtpCodeRepository;
import com.example.otpservice.repository.OtpConfigRepository;
import com.example.otpservice.util.OTPGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final OtpConfigRepository otpConfigRepository;
    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;
    private final FileStorageService fileStorageService;
    private final TelegramNotificationService telegramService;

    public OtpService(OtpCodeRepository otpCodeRepository,
                      OtpConfigRepository otpConfigRepository,
                      EmailNotificationService emailService,
                      SmsNotificationService smsService,
                      FileStorageService fileStorageService,
                      TelegramNotificationService telegramService) {
        this.otpCodeRepository = otpCodeRepository;
        this.otpConfigRepository = otpConfigRepository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.fileStorageService = fileStorageService;
        this.telegramService = telegramService;
    }

    @Transactional
    public OtpCode generateOtp(User user, String operationId, String deliveryChannel) {
        OtpConfig config = otpConfigRepository.findFirst()
                .orElseGet(() -> {
                    OtpConfig defaultConfig = new OtpConfig();
                    defaultConfig.setTtlSeconds(300);
                    defaultConfig.setCodeLength(6);
                    return otpConfigRepository.save(defaultConfig);
                });

        String code = OTPGenerator.generateNumericCode(config.getCodeLength());

        OtpCode otpCode = OtpCode.builder()
                .code(code)
                .operationId(operationId)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(config.getTtlSeconds()))
                .status(OtpStatus.ACTIVE)
                .deliveryChannel(deliveryChannel)
                .build();

        OtpCode savedOtp = otpCodeRepository.save(otpCode);

        sendOtpThroughChannel(user, code, deliveryChannel);

        log.info("OTP generated for user: {}, operation: {}, channel: {}",
                user.getUsername(), operationId, deliveryChannel);

        return savedOtp;
    }

    private void sendOtpThroughChannel(User user, String code, String channel) {
        switch (channel.toLowerCase()) {
            case "email":
                if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                    emailService.sendCode(user.getEmail(), code);
                } else {
                    log.warn("User {} has no email configured", user.getUsername());
                }
                break;
            case "sms":
                if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                    smsService.sendCode(user.getPhoneNumber(), code);
                } else {
                    log.warn("User {} has no phone number configured", user.getUsername());
                }
                break;
            case "telegram":
                if (user.getTelegramId() != null && !user.getTelegramId().isEmpty()) {
                    telegramService.sendCode(user.getTelegramId(), code);
                } else {
                    log.warn("User {} has no telegram id configured", user.getUsername());
                }
                break;
            case "file":
                fileStorageService.saveCode(user.getUsername(), code);
                break;
            default:
                log.warn("Unknown delivery channel: {}, saving to file as fallback", channel);
                fileStorageService.saveCode(user.getUsername(), code);
        }
    }

    @Transactional
    public boolean validateOtp(String code, String operationId) {
        Optional<OtpCode> otpOpt = otpCodeRepository.findByCodeAndOperationId(code, operationId);

        if (otpOpt.isEmpty()) {
            log.warn("OTP not found: {} for operation: {}", code, operationId);
            return false;
        }

        OtpCode otp = otpOpt.get();

        if (otp.getStatus() == OtpStatus.USED) {
            log.warn("OTP already used: {} for operation: {}", code, operationId);
            return false;
        }

        if (otp.getStatus() == OtpStatus.EXPIRED ||
                LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            log.warn("OTP expired: {} for operation: {}", code, operationId);
            otpCodeRepository.updateStatus(otp.getId(), OtpStatus.EXPIRED);
            return false;
        }

        otpCodeRepository.updateStatus(otp.getId(), OtpStatus.USED);
        log.info("OTP validated successfully: {} for operation: {}", code, operationId);
        return true;
    }

    @Transactional
    public void cleanupExpiredCodes() {
        List<OtpCode> expiredCodes = otpCodeRepository.findExpiredCodes();
        if (!expiredCodes.isEmpty()) {
            log.info("Found {} expired OTP codes to clean up", expiredCodes.size());
            for (OtpCode otp : expiredCodes) {
                otpCodeRepository.updateStatus(otp.getId(), OtpStatus.EXPIRED);
                log.debug("Marked OTP as expired: {} for user: {}",
                        otp.getCode(), otp.getUser().getUsername());
            }
        }
    }

    public Optional<OtpConfig> getOtpConfig() {
        return otpConfigRepository.findFirst();
    }

    @Transactional
    public OtpConfig updateOtpConfig(Integer ttlSeconds, Integer codeLength) {
        OtpConfig config = otpConfigRepository.findFirst()
                .orElseGet(OtpConfig::new);

        config.setTtlSeconds(ttlSeconds);
        config.setCodeLength(codeLength);

        return otpConfigRepository.save(config);
    }
}