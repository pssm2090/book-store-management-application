package com.bookstore.exception;

public class DuplicateCategoryNameException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateCategoryNameException(String message) {
        super(message);
    }
}
