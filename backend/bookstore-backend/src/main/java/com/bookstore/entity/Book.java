package com.bookstore.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

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
    private String author; 

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;   

    @Column(unique = true, length = 20)
    private String isbn;    

    @PastOrPresent(message = "Published date cannot be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate publishedDate;  

    @Column(nullable = false)
    private Integer stockQuantity;   

    @Valid
    @ManyToOne(fetch = FetchType.EAGER)  
    @JoinColumn(name = "categoryId") 
    private Category category;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;  

    private LocalDateTime updatedAt; 
  

    
    public Book() {}



    public Book(String title, String author, String description, BigDecimal price, String isbn,
			LocalDate publishedDate, Integer stockQuantity, Category category, String coverImageUrl) {
		this.title = title;
		this.author = author;
		this.description = description;
		this.price = price;
		this.isbn = isbn;
		this.publishedDate = publishedDate;
		this.stockQuantity = stockQuantity;
		this.category = category;
		this.coverImageUrl = coverImageUrl;
	}




    public Long getBookId() {
        return bookId;
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

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    

    
    @Override
    public String toString() {
        return "Book{" +
               "bookId=" + bookId +
               ", title='" + title + '\'' +
               ", author='" + author + '\'' +
               ", price=" + price +
               ", isbn='" + isbn + '\'' +
               '}';
    }
}
