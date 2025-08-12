package com.bookstore.dto.cart;

import java.math.BigDecimal;

public class CartItemDTO {

    private Long cartItemId;
    private Long bookId;
    private String title;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;

    // Constructors
    public CartItemDTO() {}

    public CartItemDTO(Long cartItemId, Long bookId, String title, BigDecimal price, int quantity, BigDecimal subtotal) {
        this.cartItemId = cartItemId;
        this.bookId = bookId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // Getters and Setters
    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
