package com.bookstore.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddToCartRequestDTO {

    @NotNull(message = "Book ID is required")
    private Long bookId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    // Constructors
    public AddToCartRequestDTO() {}

    public AddToCartRequestDTO(Long bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
