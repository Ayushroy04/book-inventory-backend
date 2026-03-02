package com.example.book_inventory.dto.response.cart;

import com.example.book_inventory.model.Cart.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private String CartId;
    private String userId;
    private String status;

    private List<CartItemResponse> items;

    private Double totalAmount;
    private int totalItems;

}
