package com.bookstore.dto.book;

public class BookInventoryDTO {

    private Long bookId;
    private String title;
    private String author;
    private int stockQuantity;

    public BookInventoryDTO() {
    }

    public BookInventoryDTO(Long bookId, String title, String author, int stockQuantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.stockQuantity = stockQuantity;
    }

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

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
