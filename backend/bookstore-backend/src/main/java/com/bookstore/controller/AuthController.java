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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
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
        return ResponseEntity.ok(userService.registerAndGenerateTokens(userRequest));
    }



    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginAndGenerateTokens(request));
    }






    @GetMapping("/get/profile")
    public ResponseEntity<LoginResponse> getProfile(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(userService.getProfileFromToken(token));
    }




    @PutMapping("/update/profile")
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

    
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get/users")
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {

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


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/get/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        return ResponseEntity.ok(userService.getUserStats());
    }

    
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        LoginResponse response = userService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

}
