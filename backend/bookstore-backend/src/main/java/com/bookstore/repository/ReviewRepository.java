package com.bookstore.repository;

import com.bookstore.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookBookId(Long bookId);
    
    List<Review> findByUserUserId(Long userId);
    
    @Query("SELECT r.book, AVG(r.rating) as avgRating " +
            "FROM Review r " +
            "GROUP BY r.book " +
            "ORDER BY avgRating DESC")
     List<Object[]> findTopRatedBooks(Pageable pageable);
}
