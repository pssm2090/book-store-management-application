package com.bookstore.service;

import com.bookstore.dto.cart.AddToCartRequestDTO;
import com.bookstore.dto.cart.CartItemDTO;
import com.bookstore.dto.cart.CartResponseDTO;
import com.bookstore.dto.cart.UpdateCartItemRequestDTO;
import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.User;
import com.bookstore.exception.*;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.CartItemRepository;
import com.bookstore.repository.CartRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

	@Autowired
    private CartRepository cartRepository;
    
	@Autowired
	private CartItemRepository cartItemRepository;
    
	@Autowired
	private BookRepository bookRepository;
    
	@Autowired
	private UserService userService;

	public CartResponseDTO addToCart(AddToCartRequestDTO request) {
	    User user = userService.getCurrentUser();
	    Book book = bookRepository.findById(request.getBookId())
	            .orElseThrow(() -> new BookNotFoundException("Book not found"));

	    // Stock check
	    if (request.getQuantity() > book.getStockQuantity()) {
	        throw new InsufficientStockException(
	            "Only " + book.getStockQuantity() + " copies available for " + book.getTitle()
	        );
	    }

	    Cart cart = cartRepository.findByUser(user)
	            .orElseGet(() -> cartRepository.save(new Cart(user)));

	    CartItem cartItem = cart.getItems().stream()
	            .filter(item -> item.getBook().getBookId().equals(request.getBookId()))
	            .findFirst()
	            .orElse(null);

	    if (cartItem != null) {
	        int newQuantity = cartItem.getQuantity() + request.getQuantity();

	        if (newQuantity > book.getStockQuantity()) {
	            throw new InsufficientStockException(
	                "You can only add up to " + book.getStockQuantity() + " copies of " + book.getTitle()
	            );
	        }

	        cartItem.setQuantity(newQuantity);
	    } else {
	        cartItem = new CartItem();
	        cartItem.setBook(book);
	        cartItem.setQuantity(request.getQuantity());
	        cart.addItem(cartItem);
	    }

	    cartRepository.save(cart);
	    return buildCartResponse(cart);
	}


	public CartResponseDTO updateCartItemQuantity(UpdateCartItemRequestDTO request) {
	    User user = userService.getCurrentUser();

	    CartItem cartItem = cartItemRepository.findById(request.getCartItemId())
	            .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

	    if (!cartItem.getCart().getUser().getUserId().equals(user.getUserId())) {
	        throw new UnauthorizedCartAccessException("Cart item does not belong to the user");
	    }

	    Book book = cartItem.getBook();
	    if (request.getQuantity() > book.getStockQuantity()) {
	        throw new InsufficientStockException(
	            "Only " + book.getStockQuantity() + " copies available for " + book.getTitle()
	        );
	    }

	    cartItem.setQuantity(request.getQuantity());
	    cartItemRepository.save(cartItem);

	    return buildCartResponse(cartItem.getCart());
	}



    public CartResponseDTO getUserCart() {
        User user = userService.getCurrentUser();
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        return buildCartResponse(cart);
    }

    public CartResponseDTO removeCartItem(Long cartItemId) {
        User user = userService.getCurrentUser();

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getUser().getUserId().equals(user.getUserId())) {
            throw new UnauthorizedCartAccessException("Cart item does not belong to the user");
        }

        Cart cart = cartItem.getCart();
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);

        return buildCartResponse(cart);
    }

    public void clearCart() {
        User user = userService.getCurrentUser(); // gets currently logged-in user
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cartRepository.save(cart);
    }


    private CartResponseDTO buildCartResponse(Cart cart) {
        List<CartItemDTO> items = cart.getItems().stream()
                .map(item -> new CartItemDTO(
                        item.getCartItemId(),
                        item.getBook().getBookId(),
                        item.getBook().getTitle(),
                        item.getBook().getPrice(),
                        item.getQuantity(),
                        item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                )).collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new CartResponseDTO(
        	    cart.getCartId(),
        	    cart.getUser().getUserId(),
        	    items,
        	    totalAmount
        	);

    }
}
