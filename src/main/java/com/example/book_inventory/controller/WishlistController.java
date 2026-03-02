package com.example.book_inventory.controller;

import com.example.book_inventory.dto.response.wishlist.WishlistResponse;
import com.example.book_inventory.service.wishlist.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<WishlistResponse> getWishlist() {
        return new ResponseEntity<>(wishlistService.getWishlist(), HttpStatus.OK);
    }

    @PostMapping("/books/{bookId}")
    public ResponseEntity<WishlistResponse> addBook(@PathVariable String bookId) {
        return new ResponseEntity<>(wishlistService.addBook(bookId), HttpStatus.OK);
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<WishlistResponse> removeBook(@PathVariable String bookId) {
        return new ResponseEntity<>(wishlistService.removeBook(bookId), HttpStatus.OK);
    }
}
