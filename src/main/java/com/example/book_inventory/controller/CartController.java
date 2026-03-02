package com.example.book_inventory.controller;

import com.example.book_inventory.dto.request.cart.AddToCartRequest;
import com.example.book_inventory.dto.request.cart.UpdateCartItemRequest;
import com.example.book_inventory.dto.response.cart.CartResponse;
import com.example.book_inventory.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/cart")
@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    public ResponseEntity<CartResponse> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<CartResponse> addToCart(@PathVariable String userId,
            @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    @PutMapping("/{userId}/items")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable String userId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, request));
    }

    @DeleteMapping("/{userId}/book/{bookId}")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<Void> removeCartItem(@PathVariable String userId, @PathVariable String bookId) {
        cartService.removeCartItem(userId, bookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
