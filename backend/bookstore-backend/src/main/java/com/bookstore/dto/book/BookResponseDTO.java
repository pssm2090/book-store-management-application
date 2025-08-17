package com.bookstore.dto.book;

import com.bookstore.entity.Book;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookResponseDTO {

	private Long bookId;
    private String title;
    private String author;
    private String description;
    private BigDecimal price;
    private String isbn;
    private LocalDate publishedDate;
    private Integer stockQuantity;
    private String categoryName;
    private String coverImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BookResponseDTO(Book book) {
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.description = book.getDescription();
        this.price = book.getPrice();
        this.isbn = book.getIsbn();
        this.publishedDate = book.getPublishedDate();
        this.stockQuantity = book.getStockQuantity();
        this.coverImageUrl = book.getCoverImageUrl();
        this.categoryName = book.getCategory() != null ? book.getCategory().getName() : null;
        this.bookId = book.getBookId();
        this.createdAt = book.getCreatedAt();
        this.updatedAt = book.getUpdatedAt();
    }



    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
