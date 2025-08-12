package com.bookstore.exception;

public class InvalidCategoryException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidCategoryException(String message) {
        super(message);
    }
}
