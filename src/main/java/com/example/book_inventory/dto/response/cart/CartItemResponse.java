package com.example.book_inventory.dto.response.cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

    private String bookId;
    private String title;
    private int quantity;
    private Double priceAtAdding;
    private Double subTotal;


}
