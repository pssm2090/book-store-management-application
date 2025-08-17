package com.bookstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating can be at most 5")
    @Column(nullable = false)
    private double rating;

    @Size(max = 1000, message = "Comment can’t be more than 1000 characters")
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime reviewDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId", nullable = false)
    private User user;


    
    public Review() {}

    public Review(double rating, String comment, Book book, User user) {
        this.rating = rating;
        this.comment = comment;
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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    
    @PrePersist
    protected void onCreate() {
        this.reviewDate = LocalDateTime.now();
    }


    
    @Override
    public String toString() {
        return "Review{" +
               "id=" + reviewId +
               ", rating=" + rating +
               ", comment='" + comment + '\'' +
               ", reviewDate=" + reviewDate +
               '}';
    }
}
