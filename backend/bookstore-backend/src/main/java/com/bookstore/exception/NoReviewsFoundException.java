package com.bookstore.exception;

public class NoReviewsFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoReviewsFoundException(String message) {
        super(message);
    }
}
