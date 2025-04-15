package com.eveiled.otp.service;

import com.eveiled.otp.dao.UserDao;
import com.eveiled.otp.model.User;
import com.eveiled.otp.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserDao userDao;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Регистрирует нового пользователя.
     * Проверяет уникальность логина и наличие администратора.
     */
    public void register(String username, String rawPassword, String role) {
        Optional<User> existingUser = userDao.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new RuntimeException("Пользователь с таким логином уже существует.");
        }

        if ("ADMIN".equalsIgnoreCase(role) && adminExists()) {
            throw new RuntimeException("Администратор уже зарегистрирован.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User(0, username, hashedPassword, role.toUpperCase());
        userDao.save(newUser);
    }

    /**
     * Аутентифицирует пользователя по логину и паролю.
     * Возвращает JWT при успешной проверке.
     */
    public String login(String username, String password) {
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Неверный пароль");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }

    /**
     * Проверяет, существует ли уже зарегистрированный администратор.
     */
    public boolean adminExists() {
        return userDao.findAllNonAdmins().stream()
                .anyMatch(user -> "ADMIN".equalsIgnoreCase(user.getRole()));
    }
}
