package com.example.book_inventory.dto.response.wishlist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistResponse {

    private String wishlistId;
    private String userId;
    private List<WishlistBookItem> books;
    private int totalItems;
}
