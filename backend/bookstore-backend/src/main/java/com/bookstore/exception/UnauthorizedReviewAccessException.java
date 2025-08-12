package com.bookstore.exception;

public class UnauthorizedReviewAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public UnauthorizedReviewAccessException(String message) {
        super(message);
    }
}
