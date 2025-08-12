package com.bookstore.exception;

public class DuplicateISBNException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateISBNException(String message) {
        super(message);
    }
}
