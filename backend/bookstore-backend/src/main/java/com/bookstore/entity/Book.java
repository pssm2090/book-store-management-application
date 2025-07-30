package com.bookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity                    
@Table(name = "books")     
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long bookId;   

    @Column(nullable = false, length = 255)
    private String title; 

    @Column(nullable = false, length = 255)
    private String author; // 

    @Column(length = 1000)
    private String description; 

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;   

    @Column(unique = true, length = 20)
    private String isbn;    

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate publishedDate;  

    @Column(nullable = false)
    private Integer stockQuantity;   

    @ManyToOne(fetch = FetchType.EAGER)  
    @JoinColumn(name = "categoryId")  
    private Category category;

//    @Column(length = 500)
//    private String coverImageUrl;  //will cover later

    @Column(updatable = false)
    private LocalDateTime createdAt;  

    private LocalDateTime updatedAt; 

    // ========= Constructors ========= //
    public Book() {}

    public Book(String title, String author, BigDecimal price, String isbn, 
                LocalDate publishedDate) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.isbn = isbn;
        this.publishedDate = publishedDate;
    }

    // ========= Getters & Setters ========= //

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

//    public String getCoverImageUrl() {
//        return coverImageUrl;
//    }
//
//    public void setCoverImageUrl(String coverImageUrl) {
//        this.coverImageUrl = coverImageUrl;
//    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ========= Lifecycle Callbacks ========= //

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
