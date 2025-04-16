package com.eveiled.otp.dao;

import com.eveiled.otp.model.OtpCode;
import com.eveiled.otp.model.OtpConfigDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OtpCodeCleanerService {

    private final OtpCodeDao otpCodeDao;
    private final OtpConfigDao configDao;

    public OtpCodeCleanerService(OtpCodeDao otpCodeDao, OtpConfigDao configDao) {
        this.otpCodeDao = otpCodeDao;
        this.configDao = configDao;
    }

    @Scheduled(fixedRate = 10000)
    public void expireOldCodes() {
        List<OtpCode> activeCodes = otpCodeDao.findAllActive();
        Optional<OtpConfigDto> configOpt = configDao.getConfig();
        if (configOpt.isEmpty()) return;

        int ttlSeconds = configOpt.get().ttlSeconds();
        LocalDateTime now = LocalDateTime.now();

        for (OtpCode code : activeCodes) {
            if (code.getCreatedAt().plusSeconds(ttlSeconds).isBefore(now)) {
                otpCodeDao.updateStatus(code.getId(), "EXPIRED");
                log.info("[OTP] Код #{} просрочен и помечен как EXPIRED", code.getId());
            }
        }
    }
}

