package com.example.book_inventory.model.Cart;

public enum CartStatus {
    ACTIVE, // Proceed to Checkout
    CHECKED_OUT, // Payment Successful and not cart not Editable
    CANCELLED,
}
