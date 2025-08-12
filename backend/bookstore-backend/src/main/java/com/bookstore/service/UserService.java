package com.bookstore.service;

import com.bookstore.dto.auth.UserRegisterDTO;
import com.bookstore.dto.auth.UserUpdateDTO;
import com.bookstore.entity.Role;
import com.bookstore.entity.User;
import com.bookstore.exception.EmailAlreadyExistsException;
import com.bookstore.exception.InvalidCredentialsException;
import com.bookstore.exception.UserNotFoundException;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(UserRegisterDTO dto) {
    	if (userRepository.existsByEmail(dto.getEmail())) {
    	    throw new EmailAlreadyExistsException("Email already in use");
    	}

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(hashedPassword);

        user.setRole(dto.getRole() != null ? dto.getRole() : Role.CUSTOMER);

        return userRepository.save(user);
    }

    public User validateUser(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
        		.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return user;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
        		.orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User updateUser(String email, UserUpdateDTO dto) {
        User user = getByEmail(email);

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
        	String hashed = passwordEncoder.encode(dto.getPassword());
            user.setPassword(hashed);
        }

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long getUserStats() {
        return userRepository.count();
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // assuming email is username
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
