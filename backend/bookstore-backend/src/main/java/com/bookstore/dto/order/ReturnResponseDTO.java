package com.bookstore.dto.order;

import com.bookstore.entity.ReturnStatus;

import java.time.LocalDateTime;
import java.util.Map;

public class ReturnResponseDTO {

    private Long id;
    private Long orderId;
    private Long userId;
    private Map<Long, Integer> bookIdToQuantity;
    private String reason;
    private ReturnStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String adminResponse;

    public ReturnResponseDTO() {}

    public ReturnResponseDTO(Long id, Long orderId, Long userId, Map<Long, Integer> bookIdToQuantity, String reason,
                             ReturnStatus status, LocalDateTime requestedAt, LocalDateTime processedAt,
                             String adminResponse) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.bookIdToQuantity = bookIdToQuantity;
        this.reason = reason;
        this.status = status;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.adminResponse = adminResponse;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReason() {
        return reason;
    }

    public ReturnStatus getStatus() {
        return status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public String getAdminResponse() {
        return adminResponse;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(ReturnStatus status) {
        this.status = status;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
    }
    
    public Map<Long, Integer> getBookIdToQuantity() { 
    	return bookIdToQuantity; 
    }
    
    public void setBookIdToQuantity(Map<Long, Integer> bookIdToQuantity) {
        this.bookIdToQuantity = bookIdToQuantity;
    }

}
