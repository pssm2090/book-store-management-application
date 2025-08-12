package com.bookstore.exception;

public class FileParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FileParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
