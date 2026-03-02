package com.example.book_inventory.service.wishlist;

import com.example.book_inventory.dto.response.wishlist.WishlistResponse;

public interface WishlistService {

    WishlistResponse getWishlist();

    WishlistResponse addBook(String bookId);

    WishlistResponse removeBook(String bookId);
}
