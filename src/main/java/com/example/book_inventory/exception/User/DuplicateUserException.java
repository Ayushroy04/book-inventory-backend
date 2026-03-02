package com.example.book_inventory.exception.User;

public class DuplicateUserException extends RuntimeException{
    public DuplicateUserException(String message) {
        super(message);
    }
}
