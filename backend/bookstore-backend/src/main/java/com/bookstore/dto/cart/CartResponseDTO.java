package com.bookstore.dto.cart;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDTO {

    private Long cartId;
    private Long userId;
    private List<CartItemDTO> items;
    private BigDecimal grandTotal;


    
    public CartResponseDTO() {}

    public CartResponseDTO(Long cartId, Long userId, List<CartItemDTO> items, BigDecimal grandTotal) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
        this.grandTotal = grandTotal;
    }


    
    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }
}