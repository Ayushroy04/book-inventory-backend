package com.example.book_inventory.model.Cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {

    private String bookId;
    private String title;
    private String author;
    private int quantity;
    private Double priceAtAdding;
    private Double subTotal;

}
