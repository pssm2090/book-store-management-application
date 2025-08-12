package com.bookstore.entity;

import jakarta.persistence.*;
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

    @NotBlank(message = "Book title is required")
    @Size(max = 255, message = "Title can’t be more than 255 characters")
    @Column(nullable = false, length = 255)
    private String title; 

    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author name can’t be more than 255 characters")
    @Column(nullable = false, length = 255)
    private String author; 

    @Size(max = 1000, message = "Description can’t be more than 1000 characters")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Price must be a valid amount")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;   

    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 20, message = "ISBN must be between 10 to 20 characters")
    @Column(unique = true, length = 20)
    private String isbn;    

    @PastOrPresent(message = "Published date cannot be in the future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate publishedDate;  

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(nullable = false)
    private Integer stockQuantity;   

    @NotNull(message = "Book category is required")
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



//    public Book(Long bookId, String title, String author, String description, BigDecimal price, String isbn,
//			LocalDate publishedDate, Integer stockQuantity, Category category) {
//		this.bookId = bookId;
//		this.title = title;
//		this.author = author;
//		this.description = description;
//		this.price = price;
//		this.isbn = isbn;
//		this.publishedDate = publishedDate;
//		this.stockQuantity = stockQuantity;
//		this.category = category;
//	}



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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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
    
    // ========= To String ========= //

    
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
