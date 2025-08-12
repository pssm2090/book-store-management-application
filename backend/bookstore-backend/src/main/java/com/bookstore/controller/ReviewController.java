package com.bookstore.controller;

import com.bookstore.entity.Review;
import com.bookstore.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * Add a new review for a book.
     * The Review JSON must include a nested Book object with only bookId set.
     */
    @PostMapping
    public ResponseEntity<Review> addReview(@Valid @RequestBody Review review,
                                            @AuthenticationPrincipal User currentUser) {
        Review savedReview = reviewService.addReview(review, currentUser.getUsername());
        return ResponseEntity.ok(savedReview);
    }

    /**
     * Get all reviews for a specific book.
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Review>> getReviewsByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBook(bookId));
    }

    /**
     * Get all reviews submitted by a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    /**
     * Delete a review by its ID.
     * Only the review author (logged-in user) is allowed to delete it.
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                                               @AuthenticationPrincipal User currentUser) {
        reviewService.deleteReview(reviewId, currentUser.getUsername());
        return ResponseEntity.ok("Review deleted successfully");
    }
    
    @GetMapping("/book/{bookId}/average-rating")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {
        double average = reviewService.getAverageRatingForBook(bookId);
        return ResponseEntity.ok(average);
    }

}
