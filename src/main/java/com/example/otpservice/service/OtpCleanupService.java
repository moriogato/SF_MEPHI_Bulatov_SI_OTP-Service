package com.example.otpservice.service;

import com.example.otpservice.entity.OtpCode;
import com.example.otpservice.entity.OtpStatus;
import com.example.otpservice.repository.OtpCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OtpCleanupService {

    private final OtpCodeRepository otpCodeRepository;

    public OtpCleanupService(OtpCodeRepository otpCodeRepository) {
        this.otpCodeRepository = otpCodeRepository;
    }


    @Scheduled(fixedDelayString = "${otp.cleanup.interval:60000}", initialDelay = 10000)
    @Transactional
    public void cleanupExpiredCodes() {
        try {
            log.debug("Starting OTP cleanup job...");

            List<OtpCode> expiredCodes = otpCodeRepository.findExpiredCodes();

            if (expiredCodes.isEmpty()) {
                log.debug("No expired OTP codes found");
                return;
            }

            log.info("Found {} expired OTP codes to clean up", expiredCodes.size());

            int updatedCount = 0;
            for (OtpCode otpCode : expiredCodes) {
                otpCodeRepository.updateStatus(otpCode.getId(), OtpStatus.EXPIRED);
                updatedCount++;
                log.debug("Marked OTP as expired: code={}, user={}, operation={}",
                        otpCode.getCode(),
                        otpCode.getUser().getUsername(),
                        otpCode.getOperationId()
                );
            }

            log.info("Successfully marked {} OTP codes as EXPIRED", updatedCount);

        } catch (Exception e) {
            log.error("Error during OTP cleanup job: {}", e.getMessage(), e);
        }
    }
}