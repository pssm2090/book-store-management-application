package com.bookstore.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateCartItemRequestDTO {

    @NotNull(message = "Cart item ID is required")
    private Long cartItemId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;


    
    
    public UpdateCartItemRequestDTO() {}

    public UpdateCartItemRequestDTO(Long cartItemId, int quantity) {
        this.cartItemId = cartItemId;
        this.quantity = quantity;
    }


    
    
    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
