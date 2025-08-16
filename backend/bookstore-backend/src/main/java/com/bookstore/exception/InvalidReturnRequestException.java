package com.bookstore.exception;

public class InvalidReturnRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public InvalidReturnRequestException(String msg) {
        super(msg);
    }
}
