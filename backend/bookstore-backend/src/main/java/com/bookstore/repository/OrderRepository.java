package com.bookstore.repository;

import com.bookstore.entity.Order;
import com.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user); // Fetch all orders for a specific user

    List<Order> findByUserUserId(Long userId); // Alternative if you prefer using userId directly

    List<Order> findByPlacedAtAfter(LocalDateTime dateTime);
    
    @Query("SELECT o.user.email, COUNT(o.id), SUM(o.totalAmount) " +
            "FROM Order o " +
            "GROUP BY o.user.email " +
            "ORDER BY SUM(o.totalAmount) DESC")
     List<Object[]> findTopCustomers();
     
    @Query("SELECT FUNCTION('DATE_FORMAT', o.placedAt, '%d-%m-%Y') AS month, COUNT(o), SUM(o.totalAmount) " +
    	       "FROM Order o GROUP BY month ORDER BY month")
    List<Object[]> getMonthlyTrends();

    @Query("SELECT FUNCTION('YEARWEEK', o.placedAt) AS week, COUNT(o), SUM(o.totalAmount) " +
    	       "FROM Order o GROUP BY week ORDER BY week")
    List<Object[]> getWeeklyTrends();

    @Query("SELECT DATE(o.placedAt), COUNT(o), SUM(o.totalAmount) " +
    	       "FROM Order o GROUP BY DATE(o.placedAt) ORDER BY DATE(o.placedAt)")
    List<Object[]> getDailyTrends();

    @Query("SELECT o.user.email, DATE(o.placedAt), COUNT(o.orderId), SUM(o.totalAmount) " +
    	       "FROM Order o GROUP BY o.user.email, DATE(o.placedAt) " +
    	       "ORDER BY o.user.email, DATE(o.placedAt)")
    List<Object[]> getUserPurchaseTrends();

    @Query("SELECT DISTINCT o.user FROM Order o")
    List<User> findAllDistinctUsersWithOrders();
    
    
}
