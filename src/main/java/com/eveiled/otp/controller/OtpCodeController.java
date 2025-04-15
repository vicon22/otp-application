package com.eveiled.otp.controller;

import com.eveiled.otp.dao.UserDao;
import com.eveiled.otp.model.OtpGenerateRequest;
import com.eveiled.otp.model.OtpResponse;
import com.eveiled.otp.model.OtpValidationRequest;
import com.eveiled.otp.model.User;
import com.eveiled.otp.service.OtpCodeService;
import com.eveiled.otp.util.AuthHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/otp")
public class OtpCodeController {

    private final OtpCodeService otpService;
    private final UserDao userDao;
    private final AuthHelper authHelper;

    public OtpCodeController(OtpCodeService otpService, UserDao userDao, AuthHelper authHelper) {
        this.otpService = otpService;
        this.userDao = userDao;
        this.authHelper = authHelper;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody OtpGenerateRequest request) {
        String username = authHelper.getCurrentUsername();
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String code = otpService.generateOtp(user.getId(), request.operationId());

        // Каналы рассылки (заглушки для начала)
        switch (request.channel().toLowerCase()) {
            case "file" -> otpService.sendToFile(user.getUsername(), code);
            case "email" -> otpService.sendToEmail(request.destination(), code);
            case "sms" -> otpService.sendToSms(request.destination(), code);
            case "telegram" -> otpService.sendToTelegram(request.destination(), code);
            default -> throw new RuntimeException("Неверный канал доставки");
        }

        return ResponseEntity.ok("OTP-код отправлен через " + request.channel());
    }

    @PostMapping("/validate")
    public ResponseEntity<OtpResponse> validate(@RequestBody OtpValidationRequest request) {
        String username = authHelper.getCurrentUsername();
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        boolean valid = otpService.validateOtp(user.getId(), request.operationId(), request.code());
        return valid
                ? ResponseEntity.ok(new OtpResponse("Код подтвержден."))
                : ResponseEntity.status(400).body(new OtpResponse("Код недействителен или просрочен."));
    }
}


