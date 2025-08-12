package com.bookstore.controller;

import com.bookstore.dto.auth.LoginRequest;
import com.bookstore.dto.auth.LoginResponse;
import com.bookstore.dto.auth.RefreshTokenRequest;
import com.bookstore.dto.auth.UserRegisterDTO;
import com.bookstore.dto.auth.UserUpdateDTO;
import com.bookstore.entity.User;
import com.bookstore.service.UserService;
import com.bookstore.security.JwtService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody UserRegisterDTO userRequest) {

    	User savedUser = userService.register(userRequest);

        String accessToken = jwtService.generateAccessToken(
            savedUser.getEmail(),
            savedUser.getRole().name(),
            savedUser.getName()
        );

        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail());

        LoginResponse response = new LoginResponse(
            accessToken,
            refreshToken,
            "Registration successful",
            savedUser.getEmail(),
            savedUser.getName(),
            savedUser.getRole().name()
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.validateUser(request.getEmail(), request.getPassword());

        String accessToken = jwtService.generateAccessToken(
            user.getEmail(),
            user.getRole().name(),
            user.getName()
        );

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        LoginResponse response = new LoginResponse(
            accessToken,
            refreshToken,
            "Login successful",
            user.getEmail(),
            user.getName(),
            user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }




    @GetMapping("/profile")
    public ResponseEntity<LoginResponse> getProfile(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);

        String email = jwtService.extractEmail(accessToken);
        String role = jwtService.extractRole(accessToken);
        String name = jwtService.extractName(accessToken);

        LoginResponse response = new LoginResponse(
            accessToken,
            null,
            "Profile fetched successfully",
            email,
            name,
            role
        );

        return ResponseEntity.ok(response);
    }



    @PutMapping("/profile/update")
    public ResponseEntity<LoginResponse> updateProfile(@RequestHeader("Authorization") String token,
                                                       @RequestBody UserUpdateDTO updateRequest) {
        String accessToken = token.substring(7);
        String email = jwtService.extractEmail(accessToken);

        User updatedUser = userService.updateUser(email, updateRequest);

        LoginResponse response = new LoginResponse(
            accessToken,
            null,
            "Profile updated successfully",
            updatedUser.getEmail(),
            updatedUser.getName(),
            updatedUser.getRole().name()
        );

        return ResponseEntity.ok(response);
    }


    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        String role = jwtService.extractRole(accessToken);

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<LoginResponse> users = userService.getAllUsers()
            .stream()
            .map(user -> new LoginResponse(
                    null,
                    null,
                    "User fetched successfully",
                    user.getEmail(),
                    user.getName(),
                    user.getRole().name()
            ))
            .toList();

        return ResponseEntity.ok(users);
    }



    @GetMapping("/admin/stats")
    public ResponseEntity<?> getUserStats(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        String role = jwtService.extractRole(accessToken);

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        return ResponseEntity.ok(userService.getUserStats());
    }



    
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        try {
            if (jwtService.isTokenExpired(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String email = jwtService.extractEmail(refreshToken);
            String role = jwtService.extractRole(refreshToken);
            String name = jwtService.extractName(refreshToken);

            String newAccessToken = jwtService.generateAccessToken(email, role, name);

            LoginResponse response = new LoginResponse(
                newAccessToken,
                refreshToken, // reusing existing refresh token
                "Token refreshed successfully",
                email,
                name,
                role
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }


}
