package com.example.book_inventory.utils;

import com.example.book_inventory.dto.response.book.BookResponse;
import com.example.book_inventory.model.Book.BookDocument;
import com.example.book_inventory.model.Cart.CartDocument;
import com.example.book_inventory.model.Cart.CartStatus;
import com.example.book_inventory.repository.CartRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class InventoryVisibilityHelper {

    public void apply(BookDocument bookDocument, BookResponse response) {

        Integer availableStock = bookDocument.getAvailableStock();
        // Out of stock
        if (availableStock == null || availableStock <= 0) {
            response.setOutOfStock(true);
            response.setStockMessage("Out of stock");
            return;
        }
        response.setOutOfStock(false);
        // Show stock only if a single digit
        if (availableStock <= 9) {
            response.setStockMessage("Only " + availableStock + " left");
        } else {
            response.setStockMessage(null);
        }
    }
}
