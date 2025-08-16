package com.bookstore.dto.order;

import com.bookstore.entity.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderRequestDTO {

    @NotNull(message = "Cart item IDs list must not be null")
    @NotEmpty(message = "Cart item IDs list cannot be empty")
    private List<Long> cartItemIds;

    @NotNull(message = "Payment method must be provided")
    private PaymentMethod paymentMethod;


    
    public OrderRequestDTO() {
    }

    public OrderRequestDTO(List<Long> cartItemIds, PaymentMethod paymentMethod) {
        this.cartItemIds = cartItemIds;
        this.paymentMethod = paymentMethod;
    }


    
    public List<Long> getCartItemIds() {
        return cartItemIds;
    }

    public void setCartItemIds(List<Long> cartItemIds) {
        this.cartItemIds = cartItemIds;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
