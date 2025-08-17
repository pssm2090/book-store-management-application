package com.bookstore.repository;

import com.bookstore.entity.OrderItem;
import com.bookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByOrderOrderId(Long orderId);

    @Query("SELECT oi.book, SUM(oi.quantity) as totalPurchased " +
           "FROM OrderItem oi " +
           "GROUP BY oi.book " +
           "ORDER BY totalPurchased DESC")
    List<Object[]> findMostPurchasedBooks(Pageable pageable);

    @Query("SELECT oi.book.category.name, SUM(oi.quantity * oi.price) " +
           "FROM OrderItem oi " +
           "GROUP BY oi.book.category.name")
    List<Object[]> getRevenueByCategory();
    
    @Query("SELECT oi.book.id " +
    	       "FROM OrderItem oi " +
    	       "GROUP BY oi.book.id " +
    	       "ORDER BY SUM(oi.quantity) DESC")
    List<Long> findTopSellingBookIds(Pageable pageable);
    	
    @Query("SELECT oi.book.title, SUM(oi.quantity * oi.price) " +
    	       "FROM OrderItem oi " +
    	       "GROUP BY oi.book.title " +
    	       "ORDER BY SUM(oi.quantity * oi.price) DESC")
   	List<Object[]> getRevenuePerBook();

    @Query("SELECT FUNCTION('DATE_FORMAT', oi.order.placedAt, :pattern), SUM(oi.quantity * oi.price) " +
    	       "FROM OrderItem oi " +
    	       "GROUP BY FUNCTION('DATE_FORMAT', oi.order.placedAt, :pattern) " +
    	       "ORDER BY FUNCTION('DATE_FORMAT', oi.order.placedAt, :pattern) ASC")
   	List<Object[]> getRevenueOverTime(@Param("pattern") String pattern);
   	
    @Query("SELECT oi.book.title, DATE(o.placedAt), SUM(oi.quantity) " +
 	       "FROM OrderItem oi JOIN oi.order o " +
 	       "GROUP BY oi.book.title, DATE(o.placedAt) " +
 	       "ORDER BY DATE(o.placedAt)")
    List<Object[]> getBookPurchaseTrends();
    	
}
