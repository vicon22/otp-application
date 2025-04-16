package com.eveiled.otp.service;

import com.eveiled.otp.dao.OtpCodeDao;
import com.eveiled.otp.dao.OtpConfigDao;
import com.eveiled.otp.model.OtpCode;
import com.eveiled.otp.model.OtpConfigDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class OtpCodeService {

    private final OtpCodeDao otpDao;
    private final OtpConfigDao configDao;
    private final TelegramService telegramService;
    private final EmailService emailService;
    private final SmsService smsService;

    public OtpCodeService(OtpCodeDao otpDao,
                          OtpConfigDao configDao,
                          TelegramService telegramService,
                          EmailService emailService,
                          SmsService smsService) {
        this.otpDao = otpDao;
        this.configDao = configDao;
        this.telegramService = telegramService;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    public String generateOtp(int userId, String operationId) {
        OtpConfigDto config = configDao.getConfig()
                .orElseThrow(() -> new RuntimeException("OTP конфигурация не найдена"));

        String code = generateNumericCode(config.codeLength());
        otpDao.save(userId, code, operationId);

        // Тест: просто возвращаем (в будущем — отправка по Email/Telegram/SMS/файл)
        return code;
    }

    public boolean validateOtp(int userId, String operationId, String codeToCheck) {
        Optional<OtpCode> existing = otpDao.findActiveCode(userId, operationId);
        if (existing.isEmpty()) return false;

        OtpCode otp = existing.get();

        if (isExpired(otp)) {
            otpDao.updateStatus(otp.getId(), "EXPIRED");
            return false;
        }

        if (otp.getCode().equals(codeToCheck)) {
            otpDao.updateStatus(otp.getId(), "USED");
            return true;
        }

        return false;
    }

    private boolean isExpired(OtpCode otp) {
        OtpConfigDto config = configDao.getConfig().orElseThrow();
        LocalDateTime now = LocalDateTime.now();
        return otp.getCreatedAt().plusSeconds(config.ttlSeconds()).isBefore(now);
    }

    private String generateNumericCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public void sendToFile(String username, String code) {
        try {
            Files.writeString(Path.of(username + "_otp.txt"), "Your OTP code is: " + code);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи в файл");
        }
    }

    public void sendToEmail(String email, String code) {
        log.info("Sending OTP to email {}: {}", email, code);
        emailService.sendOtp(email, code);
    }

    public void sendToSms(String phone, String code) {
        log.info("Sending OTP via SMS to {}: {}", phone, code);
        smsService.sendOtp(phone, code);
    }

    public void sendToTelegram(String chatId, String code) {
        log.info("Sending OTP via Telegram to {}: {}", chatId, code);
        telegramService.sendOtp(chatId, code);
    }

}

