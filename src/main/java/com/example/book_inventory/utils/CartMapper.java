package com.example.book_inventory.utils;

import com.example.book_inventory.dto.response.cart.CartItemResponse;
import com.example.book_inventory.dto.response.cart.CartResponse;
import com.example.book_inventory.model.Cart.CartDocument;
import java.util.List;
import java.util.stream.Collectors;
public class CartMapper {

    public static CartResponse toResponse(CartDocument cart) {
        // Map individual items from Model to DTO

        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getBookId(),
                        item.getTitle(),
                        item.getQuantity(),
                        item.getPriceAtAdding(),
                        item.getSubTotal()
                ))
                .collect(Collectors.toList());

        // Build and return the final response
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getStatus().name(),
                itemResponses,
                cart.getTotalAmount(),
                cart.getTotalItems()
        );
    }
}