package com.example.otpservice.service;

import lombok.extern.slf4j.Slf4j;
import org.jsmpp.bean.*;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class SmsNotificationService {

    @Value("${smpp.host:localhost}")
    private String host;

    @Value("${smpp.port:2775}")
    private int port;

    @Value("${smpp.system_id:smppclient1}")
    private String systemId;

    @Value("${smpp.password:password}")
    private String password;

    @Value("${smpp.system_type:OTP}")
    private String systemType;

    @Value("${smpp.source_addr:OTPService}")
    private String sourceAddress;

    public void sendCode(String destination, String code) {
        SMPPSession session = new SMPPSession();
        try {
            BindParameter bindParameter = new BindParameter(
                    BindType.BIND_TX,
                    systemId,
                    password,
                    systemType,
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    sourceAddress
            );

            session.connectAndBind(host, port, bindParameter);

            String message = "Your verification code is: " + code;
            session.submitShortMessage(
                    systemType,
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    sourceAddress,
                    TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.UNKNOWN,
                    destination,
                    new ESMClass(),
                    (byte) 0,
                    (byte) 1,
                    null,
                    null,
                    new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT),
                    (byte) 0,
                    new GeneralDataCoding(Alphabet.ALPHA_DEFAULT),
                    (byte) 0,
                    message.getBytes(StandardCharsets.UTF_8)
            );

            log.info("SMS sent to: {}", destination);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", destination, e.getMessage());
            // Don't throw exception for SMS failures, just log
        } finally {
            if (session.getSessionState() != null) {
                session.unbindAndClose();
            }
        }
    }
}