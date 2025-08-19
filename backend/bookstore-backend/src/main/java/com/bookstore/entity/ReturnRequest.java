package com.bookstore.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "return_requests")
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnRequestId;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "return_items", joinColumns = @JoinColumn(name = "return_request_id"))
    @MapKeyColumn(name = "book_id")
    @Column(name = "quantity")
    private Map<Long, Integer> bookIdToQuantity;

    @Column(nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReturnStatus status = ReturnStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column
    private LocalDateTime processedAt;

    @Column
    private String adminResponse;


    
    public ReturnRequest() {}

    public ReturnRequest(Order order, User user, Map<Long, Integer> bookIdToQuantity, String reason) {
        this.order = order;
        this.user = user;
        this.bookIdToQuantity = bookIdToQuantity;
        this.reason = reason;
        this.status = ReturnStatus.PENDING;
    }


    
    @PrePersist
    protected void onCreate() {
        this.requestedAt = LocalDateTime.now();
    }


    
    public Long getReturnRequestId() {
        return returnRequestId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<Long, Integer> getBookIdToQuantity() {
        return bookIdToQuantity;
    }

    public void setBookIdToQuantity(Map<Long, Integer> bookIdToQuantity) {
        this.bookIdToQuantity = bookIdToQuantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }
}
