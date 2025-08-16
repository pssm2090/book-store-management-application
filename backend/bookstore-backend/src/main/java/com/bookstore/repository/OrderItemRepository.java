package com.bookstore.repository;

import com.bookstore.entity.OrderItem;
import com.bookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order); // Fetch all order items for a specific order

    List<OrderItem> findByOrderOrderId(Long orderId); // Alternative using order ID

}
