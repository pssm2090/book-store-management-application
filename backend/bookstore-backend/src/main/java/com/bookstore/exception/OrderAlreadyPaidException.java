package com.bookstore.exception;

public class OrderAlreadyPaidException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public OrderAlreadyPaidException(String message) {
        super(message);
    }
}
