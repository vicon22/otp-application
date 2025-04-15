package com.eveiled.otp.dao;

import com.eveiled.otp.model.OtpConfigDto;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class OtpConfigDao {
    private final DataSource dataSource;

    public OtpConfigDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<OtpConfigDto> getConfig() {
        String sql = "SELECT code_length, ttl_seconds FROM otp_config WHERE id = 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(new OtpConfigDto(
                        rs.getInt("code_length"),
                        rs.getInt("ttl_seconds")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void updateConfig(OtpConfigDto config) {
        String sql = "UPDATE otp_config SET code_length = ?, ttl_seconds = ? WHERE id = 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, config.codeLength());
            stmt.setInt(2, config.ttlSeconds());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

