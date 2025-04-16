package com.eveiled.otp.service;

import com.eveiled.otp.dao.OtpConfigDao;
import com.eveiled.otp.dao.UserDao;
import com.eveiled.otp.model.OtpConfigDto;
import com.eveiled.otp.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserDao userDao;
    private final OtpConfigDao configDao;

    public AdminService(UserDao userDao, OtpConfigDao configDao) {
        this.userDao = userDao;
        this.configDao = configDao;
    }

    public List<User> getAllNonAdmins() {
        return userDao.findAll().stream()
                .filter(user -> !"ADMIN".equals(user.getRole()))
                .toList();
    }

    public void deleteUser(int userId) {
        userDao.deleteById(userId);
    }

    public Optional<OtpConfigDto> getConfig() {
        return configDao.getConfig();
    }

    public void updateConfig(OtpConfigDto config) {
        configDao.updateConfig(config);
    }
}




