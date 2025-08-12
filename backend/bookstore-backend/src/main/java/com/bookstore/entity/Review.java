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
    private int rating;

    @Size(max = 1000, message = "Comment can’t be more than 1000 characters")
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime reviewDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // === Constructors ===

    public Review() {}

    public Review(int rating, String comment, Book book, User user) {
        this.rating = rating;
        this.comment = comment;
        this.book = book;
        this.user = user;
    }

    // === Getters & Setters ===

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
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

    // === Lifecycle Callbacks ===

    @PrePersist
    protected void onCreate() {
        this.reviewDate = LocalDateTime.now();
    }

    // === toString() ===

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
