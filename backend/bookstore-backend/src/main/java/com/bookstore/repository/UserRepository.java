package com.bookstore.repository;

import com.bookstore.entity.Role;
import com.bookstore.entity.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    long countByRole(Role role);
    
    List<User> findTop5ByOrderByCreatedAtDesc(); 
}
