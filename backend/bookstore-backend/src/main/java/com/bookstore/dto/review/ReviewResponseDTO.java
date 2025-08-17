package com.bookstore.dto.review;

import java.time.LocalDateTime;

import com.bookstore.dto.auth.UserSummaryDTO;
import com.bookstore.dto.book.BookSummaryDTO;

public class ReviewResponseDTO {
    private Long reviewId;
    private double rating;
    private String comment;
    private LocalDateTime reviewDate;
    private BookSummaryDTO book;
    private UserSummaryDTO user;
    
    
    public ReviewResponseDTO() {}
    
	public ReviewResponseDTO(Long reviewId, double rating, String comment, LocalDateTime reviewDate,
			BookSummaryDTO book, UserSummaryDTO user) {
		super();
		this.reviewId = reviewId;
		this.rating = rating;
		this.comment = comment;
		this.reviewDate = reviewDate;
		this.book = book;
		this.user = user;
	}


	public Long getReviewId() {
		return reviewId;
	}


	public void setReviewId(Long reviewId) {
		this.reviewId = reviewId;
	}


	public double getRating() {
		return rating;
	}


	public void setRating(double rating) {
		this.rating = rating;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public LocalDateTime getReviewDate() {
		return reviewDate;
	}


	public void setReviewDate(LocalDateTime reviewDate) {
		this.reviewDate = reviewDate;
	}


	public BookSummaryDTO getBook() {
		return book;
	}


	public void setBook(BookSummaryDTO book) {
		this.book = book;
	}


	public UserSummaryDTO getUser() {
		return user;
	}


	public void setUser(UserSummaryDTO user) {
		this.user = user;
	}

    
	
}
