package com.example.otpservice.service;

import com.example.otpservice.entity.User;
import com.example.otpservice.repository.UserRepository;
import com.example.otpservice.repository.OtpCodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;

    public UserService(@Lazy UserRepository userRepository, @Lazy OtpCodeRepository otpCodeRepository) {
        this.userRepository = userRepository;
        this.otpCodeRepository = otpCodeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User registerUser(String username, String password, String role,
                             String email, String phoneNumber, String telegramId) {
        if ("ADMIN".equalsIgnoreCase(role) && userRepository.existsByRole("ADMIN")) {
            throw new IllegalStateException("Admin user already exists");
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role.toUpperCase());
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setTelegramId(telegramId);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", username);
        return savedUser;
    }

    public List<User> getAllNonAdminUsers() {
        return userRepository.findAllNonAdminUsers();
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteOtpCodesByUserId(userId);
        userRepository.deleteById(userId);
        log.info("User deleted successfully: {}", userId);
    }
}