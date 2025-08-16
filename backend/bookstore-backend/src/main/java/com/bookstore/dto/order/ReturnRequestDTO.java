package com.bookstore.dto.order;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ReturnRequestDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotEmpty(message = "At least one book must be selected")
    private Map<@NotNull Long, @NotNull Integer> bookIdToQuantity; 

    @NotEmpty(message = "Reason for return is required")
    private String reason;

    
    
    public ReturnRequestDTO() {}

    public ReturnRequestDTO(Long orderId, Map<Long, Integer> bookIdToQuantity, String reason) {
        this.orderId = orderId;
        this.bookIdToQuantity = bookIdToQuantity;
        this.reason = reason;
    }

    
    
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
}
