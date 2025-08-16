package com.bookstore.repository;

import com.bookstore.entity.Order;
import com.bookstore.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder(Order order);
    boolean existsByOrder(Order order);
}
