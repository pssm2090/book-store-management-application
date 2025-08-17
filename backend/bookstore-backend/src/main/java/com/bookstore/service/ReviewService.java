package com.bookstore.service;

import com.bookstore.dto.auth.UserSummaryDTO;
import com.bookstore.dto.book.BookSummaryDTO;
import com.bookstore.dto.review.ReviewResponseDTO;
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

    public Review addReview(Long bookId, Review review, String userEmail) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ReviewNotFoundException("Book not found with ID: " + bookId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ReviewNotFoundException("User not found with email: " + userEmail));

        review.setBook(book);
        review.setUser(user);
        return reviewRepository.save(review);
    }

    public List<ReviewResponseDTO> getReviewsByBook(Long bookId) {
        List<Review> reviews = reviewRepository.findByBookBookId(bookId);
        if (reviews.isEmpty()) {
            throw new NoReviewsFoundException("No reviews found for book with ID: " + bookId);
        }

        return reviews.stream().map(this::mapToDTO).toList();
    }


    public List<ReviewResponseDTO> getReviewsByUser(Long userId) {
        List<Review> reviews = reviewRepository.findByUserUserId(userId);
        if (reviews.isEmpty()) {
            throw new NoReviewsFoundException("No reviews found for user with ID: " + userId);
        }

        return reviews.stream().map(this::mapToDTO).toList();
    }


    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException("Review not found with ID: " + reviewId));

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ReviewNotFoundException("User not found with email: " + userEmail));

        if (!review.getUser().getEmail().equals(userEmail) && !user.getRole().name().equals("ADMIN")) {
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
                      .mapToDouble(Review::getRating)
                      .average()
                      .getAsDouble();
    }
    
    
    private ReviewResponseDTO mapToDTO(Review review) {
        BookSummaryDTO bookDTO = new BookSummaryDTO(
            review.getBook().getTitle(),
            review.getBook().getAuthor()
        );

        UserSummaryDTO userDTO = new UserSummaryDTO(
            review.getUser().getName(),
            review.getUser().getEmail()
        );

        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setReviewId(review.getReviewId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setReviewDate(review.getReviewDate());
        dto.setBook(bookDTO);
        dto.setUser(userDTO);

        return dto;
    }

}
