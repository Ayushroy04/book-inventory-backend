package com.example.book_inventory.service.cart;

import com.example.book_inventory.dto.request.cart.AddToCartRequest;
import com.example.book_inventory.dto.request.cart.UpdateCartItemRequest;
import com.example.book_inventory.dto.response.cart.CartResponse;

public interface CartService {

    CartResponse getCart(String userId);

    CartResponse addToCart(String userId, AddToCartRequest request);

    CartResponse updateCartItem(String userId, UpdateCartItemRequest request);

    void removeCartItem(String userId, String bookId);

    void clearCart(String userId);


}
