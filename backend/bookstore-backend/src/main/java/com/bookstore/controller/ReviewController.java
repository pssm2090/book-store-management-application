package com.bookstore.controller;

import com.bookstore.dto.review.ReviewResponseDTO;
import com.bookstore.entity.Review;
import com.bookstore.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/add/{bookId}")
    public ResponseEntity<Review> addReview(@PathVariable Long bookId, @Valid @RequestBody Review review,
                                            @AuthenticationPrincipal UserDetails currentUser) {
        Review savedReview = reviewService.addReview(bookId, review, currentUser.getUsername());
        return ResponseEntity.ok(savedReview);
    }

    @GetMapping("/get/{bookId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                                               @AuthenticationPrincipal UserDetails currentUser) {
        reviewService.deleteReview(reviewId, currentUser.getUsername());
        return ResponseEntity.ok("Review deleted successfully");
    }

    @GetMapping("/get/average-rating/{bookId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {
        double average = reviewService.getAverageRatingForBook(bookId);
        return ResponseEntity.ok(average);
    }
}
