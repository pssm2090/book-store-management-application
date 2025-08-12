package com.bookstore.repository;

import com.bookstore.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookBookId(Long bookId);
    List<Review> findByUserId(Long userId);
}
