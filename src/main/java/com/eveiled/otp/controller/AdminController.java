package com.eveiled.otp.controller;

import com.eveiled.otp.model.OtpConfigDto;
import com.eveiled.otp.model.User;
import com.eveiled.otp.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(adminService.getAllNonAdmins());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("Пользователь удалён");
    }

    @GetMapping("/otp-config")
    public ResponseEntity<OtpConfigDto> getConfig() {
        return adminService.getConfig()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/otp-config")
    public ResponseEntity<String> updateConfig(@RequestBody OtpConfigDto config) {
        adminService.updateConfig(config);
        return ResponseEntity.ok("Конфигурация обновлена");
    }
}

