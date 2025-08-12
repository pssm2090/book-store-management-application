package com.bookstore.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 to 50 characters")
    private String name;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;  // Optional - only if user wants to change it

    // Constructors
    public UserUpdateDTO() {
    }

    public UserUpdateDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}