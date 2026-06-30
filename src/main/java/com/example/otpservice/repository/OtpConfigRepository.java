package com.example.otpservice.repository;

import com.example.otpservice.entity.OtpConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class OtpConfigRepository {

    private final JdbcTemplate jdbcTemplate;

    public OtpConfigRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OtpConfig> configRowMapper = (rs, rowNum) -> {
        OtpConfig config = new OtpConfig();
        config.setId(rs.getLong("id"));
        config.setTtlSeconds(rs.getInt("ttl_seconds"));
        config.setCodeLength(rs.getInt("code_length"));
        config.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
                rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return config;
    };

    public OtpConfig save(OtpConfig config) {
        if (config.getId() == null) {
            // Ensure only one record exists
            String deleteSql = "DELETE FROM otp_config";
            jdbcTemplate.update(deleteSql);

            String sql = "INSERT INTO otp_config (ttl_seconds, code_length, updated_at) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, config.getTtlSeconds(), config.getCodeLength(), LocalDateTime.now());
            Long id = jdbcTemplate.queryForObject("SELECT lastval()", Long.class);
            config.setId(id);
        } else {
            String sql = "UPDATE otp_config SET ttl_seconds=?, code_length=?, updated_at=? WHERE id=?";
            jdbcTemplate.update(sql, config.getTtlSeconds(), config.getCodeLength(), LocalDateTime.now(), config.getId());
        }
        return config;
    }

    public Optional<OtpConfig> findFirst() {
        String sql = "SELECT * FROM otp_config LIMIT 1";
        try {
            OtpConfig config = jdbcTemplate.queryForObject(sql, configRowMapper);
            return Optional.ofNullable(config);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public void deleteAll() {
        String sql = "DELETE FROM otp_config";
        jdbcTemplate.update(sql);
    }
}