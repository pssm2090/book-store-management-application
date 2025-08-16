package com.bookstore.controller;

import com.bookstore.dto.cart.AddToCartRequestDTO;
import com.bookstore.dto.cart.CartResponseDTO;
import com.bookstore.dto.cart.UpdateCartItemRequestDTO;
import com.bookstore.service.CartService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
    private CartService cartService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@Valid @RequestBody AddToCartRequestDTO request) {
        CartResponseDTO response = cartService.addToCart(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PutMapping("/update")
    public ResponseEntity<CartResponseDTO> updateCartItem(@Valid @RequestBody UpdateCartItemRequestDTO request) {
        CartResponseDTO response = cartService.updateCartItemQuantity(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/get")
    public ResponseEntity<CartResponseDTO> getCart() {
        CartResponseDTO response = cartService.getUserCart();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<CartResponseDTO> removeCartItem(@PathVariable Long cartItemId) {
        CartResponseDTO response = cartService.removeCartItem(cartItemId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
