package com.bookstore.exception;

public class UnauthorizedCartAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public UnauthorizedCartAccessException(String message) {
        super(message);
    }
}
