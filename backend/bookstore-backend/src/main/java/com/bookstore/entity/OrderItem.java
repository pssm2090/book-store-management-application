package com.bookstore.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

/**
 * Entity representing an item within an order.
 * Each OrderItem links a specific book with a quantity and price,
 * and is associated with a parent Order.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    /**
     * Many OrderItems belong to one Order.
     * Uses LAZY fetching to avoid loading the full Order unless required.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;

    /**
     * Each OrderItem is linked to one Book.
     * EAGER fetch is used because book details are often needed when accessing order items.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bookId", nullable = false)
    private Book book;

    /**
     * Quantity of the book ordered.
     * Must be at least 1.
     */
    @Min(1)
    @Column(nullable = false)
    private int quantity;

    /**
     * Price per unit of the book at the time of order.
     * Precision = 10, Scale = 2 means it supports up to 99999999.99.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // ───────────── Constructors ─────────────

    public OrderItem() {
    }

    public OrderItem(Order order, Book book, int quantity, BigDecimal price) {
        this.order = order;
        this.book = book;
        this.quantity = quantity;
        this.price = price;
    }

    // ───────────── Getters and Setters ─────────────

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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
