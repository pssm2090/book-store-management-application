package com.bookstore.dto.payment;

import com.bookstore.entity.PaymentMethod;
import com.bookstore.entity.PaymentStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class PaymentRequestDTO {

    @NotNull(message = "Order ID must not be null")
    private Long orderId;

    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    private PaymentStatus status;

    private String transactionId; 

    
    
    public PaymentRequestDTO() {}

    public PaymentRequestDTO(Long orderId, BigDecimal amount, PaymentMethod method, PaymentStatus status, String transactionId) {
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.transactionId = transactionId;
    }

    
    
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
