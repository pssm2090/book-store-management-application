package com.bookstore.controller;

import com.bookstore.entity.Review;
import com.bookstore.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add")
    public ResponseEntity<Review> addReview(@Valid @RequestBody Review review,
    		@AuthenticationPrincipal UserDetails currentUser) {
        Review savedReview = reviewService.addReview(review, currentUser.getUsername());
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Review>> getReviewsByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
    		@AuthenticationPrincipal UserDetails currentUser) {
        reviewService.deleteReview(reviewId, currentUser.getUsername());
        return ResponseEntity.ok("Review deleted successfully");
    }
    
    @GetMapping("/book/{bookId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {
        double average = reviewService.getAverageRatingForBook(bookId);
        return ResponseEntity.ok(average);
    }

}
