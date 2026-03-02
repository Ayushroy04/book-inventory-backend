package com.example.book_inventory.dto.response.wishlist;

import com.example.book_inventory.model.Book.BookGenre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishlistBookItem {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private Double price;
    private BookGenre genre;
    private Boolean outOfStock;
}
