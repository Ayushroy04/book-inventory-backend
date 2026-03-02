package com.example.book_inventory.exception.book;

public class DuplicateBookException extends  RuntimeException {
    public DuplicateBookException(String message) {
        super(message);
    }
}
