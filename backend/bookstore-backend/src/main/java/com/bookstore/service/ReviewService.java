package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Review;
import com.bookstore.entity.User;
import com.bookstore.exception.NoReviewsFoundException;
import com.bookstore.exception.ReviewNotFoundException;
import com.bookstore.exception.UnauthorizedReviewAccessException;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.ReviewRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public Review addReview(Review review, String userEmail) {
        Long bookId = review.getBook().getBookId(); // extract bookId from embedded book

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ReviewNotFoundException("Book not found with ID: " + bookId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ReviewNotFoundException("User not found with email: " + userEmail));

        review.setBook(book);
        review.setUser(user);
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByBook(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookBookId(bookId);
        if (reviews.isEmpty()) {
            throw new NoReviewsFoundException("No reviews found for book with ID: " + bookId);
        }
        return reviews;
    }


    public List<Review> getReviewsByUser(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        if (reviews.isEmpty()) {
            throw new NoReviewsFoundException("No reviews found for user with ID: " + userId);
        }
        return reviews;
    }


    public void deleteReview(Long reviewId, String userEmail) {
    	
    	Review review = reviewRepository.findById(reviewId)
    	        .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + reviewId));

    	if (!review.getUser().getEmail().equals(userEmail)) {
    	    throw new UnauthorizedReviewAccessException("You are not authorized to delete this review");
    	}


        reviewRepository.deleteById(reviewId);
    }
    
    
    public double getAverageRatingForBook(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookBookId(bookId);
        if (reviews.isEmpty()) {
            throw new NoReviewsFoundException("No reviews found for book with ID: " + bookId);
        }
        return reviews.stream()
                      .mapToInt(Review::getRating)
                      .average()
                      .orElse(0.0); // fallback, though won't happen due to empty check
    }

}
