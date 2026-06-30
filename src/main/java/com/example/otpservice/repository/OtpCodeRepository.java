package com.example.otpservice.repository;

import com.example.otpservice.entity.OtpCode;
import com.example.otpservice.entity.User;
import com.example.otpservice.entity.OtpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class OtpCodeRepository {

    private final JdbcTemplate jdbcTemplate;

    public OtpCodeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<OtpCode> otpCodeRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("user_id"));

        OtpCode otpCode = OtpCode.builder()
                .id(rs.getLong("id"))
                .code(rs.getString("code"))
                .operationId(rs.getString("operation_id"))
                .user(user)
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
                .status(OtpStatus.valueOf(rs.getString("status")))
                .deliveryChannel(rs.getString("delivery_channel"))
                .build();
        return otpCode;
    };

    public OtpCode save(OtpCode otpCode) {
        if (otpCode.getId() == null) {
            String sql = """
                INSERT INTO otp_codes (code, operation_id, user_id, created_at, expires_at, status, delivery_channel)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
            jdbcTemplate.update(sql,
                    otpCode.getCode(),
                    otpCode.getOperationId(),
                    otpCode.getUser().getId(),
                    otpCode.getCreatedAt(),
                    otpCode.getExpiresAt(),
                    otpCode.getStatus().name(),
                    otpCode.getDeliveryChannel()
            );
            Long id = jdbcTemplate.queryForObject("SELECT lastval()", Long.class);
            otpCode.setId(id);
        } else {
            String sql = "UPDATE otp_codes SET status=? WHERE id=?";
            jdbcTemplate.update(sql, otpCode.getStatus().name(), otpCode.getId());
        }
        return otpCode;
    }

    public Optional<OtpCode> findByCodeAndOperationId(String code, String operationId) {
        String sql = "SELECT * FROM otp_codes WHERE code = ? AND operation_id = ?";
        try {
            OtpCode otpCode = jdbcTemplate.queryForObject(sql, otpCodeRowMapper, code, operationId);
            return Optional.ofNullable(otpCode);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<OtpCode> findExpiredCodes() {
        String sql = "SELECT * FROM otp_codes WHERE status = 'ACTIVE' AND expires_at < ?";
        return jdbcTemplate.query(sql, otpCodeRowMapper, LocalDateTime.now());
    }

    public void updateStatus(Long id, OtpStatus status) {
        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status.name(), id);
    }

    public List<OtpCode> findByUserId(Long userId) {
        String sql = "SELECT * FROM otp_codes WHERE user_id = ?";
        return jdbcTemplate.query(sql, otpCodeRowMapper, userId);
    }
}