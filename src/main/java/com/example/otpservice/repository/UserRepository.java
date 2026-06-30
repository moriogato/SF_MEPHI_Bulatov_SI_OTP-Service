package com.example.otpservice.repository;

import com.example.otpservice.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public UserRepository(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setTelegramId(rs.getString("telegram_id"));
        user.setCreatedAt(rs.getTimestamp("created_at") != null ?
                rs.getTimestamp("created_at").toLocalDateTime() : null);
        user.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
                rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return user;
    };

    public User save(User user) {
        if (user.getId() == null) {
            String sql = """
                INSERT INTO users (username, password, role, email, phone_number, telegram_id, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
            jdbcTemplate.update(sql,
                    user.getUsername(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getRole(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getTelegramId(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            Long id = jdbcTemplate.queryForObject("SELECT lastval()", Long.class);
            user.setId(id);
        } else {
            String sql = """
                UPDATE users 
                SET username=?, password=?, role=?, email=?, phone_number=?, telegram_id=?, updated_at=?
                WHERE id=?
            """;
            jdbcTemplate.update(sql,
                    user.getUsername(),
                    passwordEncoder.encode(user.getPassword()),
                    user.getRole(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getTelegramId(),
                    LocalDateTime.now(),
                    user.getId()
            );
        }
        return user;
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, username);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean existsByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, role);
        return count != null && count > 0;
    }

    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public List<User> findAllNonAdminUsers() {
        String sql = "SELECT * FROM users WHERE role != 'ADMIN'";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteOtpCodesByUserId(Long userId) {
        String sql = "DELETE FROM otp_codes WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}