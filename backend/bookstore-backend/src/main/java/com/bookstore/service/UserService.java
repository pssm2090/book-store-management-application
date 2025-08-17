package com.bookstore.service;

import com.bookstore.dto.auth.LoginRequest;
import com.bookstore.dto.auth.LoginResponse;
import com.bookstore.dto.auth.UserRegisterDTO;
import com.bookstore.dto.auth.UserUpdateDTO;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.EmailAlreadyExistsException;
import com.bookstore.exception.InvalidCredentialsException;
import com.bookstore.exception.InvalidTokenException;
import com.bookstore.exception.UserNotFoundException;
import com.bookstore.repository.UserRepository;
import com.bookstore.security.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;


    public LoginResponse registerAndGenerateTokens(UserRegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole() != null ? dto.getRole() : Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(
            savedUser.getEmail(),
            savedUser.getRole().name(),
            savedUser.getName()
        );

        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail());

        return new LoginResponse(
            accessToken,
            refreshToken,
            "Registration successful",
            savedUser.getEmail(),
            savedUser.getName(),
            savedUser.getRole().name()
        );
    }


    public LoginResponse loginAndGenerateTokens(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateAccessToken(
            user.getEmail(),
            user.getRole().name(),
            user.getName()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginResponse(
            accessToken,
            refreshToken,
            "Login successful",
            user.getEmail(),
            user.getName(),
            user.getRole().name()
        );
    }


    
    
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public LoginResponse getProfileFromToken(String token) {
        String accessToken = token.substring(7);

        String email = jwtService.extractEmail(accessToken);
        if (email == null) {
            throw new InvalidTokenException("Invalid or expired access token");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        return new LoginResponse(
            accessToken,
            null,
            "Profile fetched successfully",
            user.getEmail(),
            user.getName(),
            user.getRole().name() // assuming enum
        );
    }

    

    public User updateUser(String email, UserUpdateDTO dto) {
        User user = getByEmail(email);

        if (dto.getOldPassword() == null || dto.getOldPassword().isBlank()) {
            throw new InvalidCredentialsException("Current password is required to update profile.");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect.");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return userRepository.save(user);
    }

    
    

    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long adminCount = userRepository.countByRole(Role.ADMIN);
        long customerCount = userRepository.countByRole(Role.CUSTOMER);

        List<Map<String, Object>> recentUsers = userRepository.findTop5ByOrderByCreatedAtDesc()
            .stream()
            .map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("email", user.getEmail());
                map.put("name", user.getName());
                map.put("role", user.getRole().name());
                map.put("createdAt", user.getCreatedAt());
                return map;
            })
            .collect(Collectors.toList());

        stats.put("totalUsers", totalUsers);
        stats.put("adminCount", adminCount);
        stats.put("customerCount", customerCount);
        stats.put("recentUsers", recentUsers);

        return stats;
    }

    public LoginResponse refreshAccessToken(String refreshToken) {
        if (jwtService.isTokenExpired(refreshToken)) {
            throw new InvalidTokenException("Refresh token expired");
        }

        String email = jwtService.extractEmail(refreshToken);
        String role = jwtService.extractRole(refreshToken);
        String name = jwtService.extractName(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(email, role, name);

        return new LoginResponse(
            newAccessToken,
            refreshToken,
            "Token refreshed successfully",
            email,
            name,
            role
        );
    }


    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return getByEmail(email);
    }
}
