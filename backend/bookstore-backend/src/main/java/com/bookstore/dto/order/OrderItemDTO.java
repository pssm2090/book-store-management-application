package com.bookstore.dto.order;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemDTO {

	@NotNull(message = "Book ID is required")
    private Long bookId;

	// Marked as READ_ONLY: included in responses, ignored in requests
    @JsonProperty(access = Access.READ_ONLY)
    private String title;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    
    @Positive(message = "Price must be greater than 0.00")
    private BigDecimal price;

    // Constructors
    public OrderItemDTO() {
    }

    public OrderItemDTO(Long bookId, String title, int quantity, BigDecimal price) {
        this.bookId = bookId;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
