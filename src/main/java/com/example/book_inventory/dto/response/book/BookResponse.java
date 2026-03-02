package com.example.book_inventory.dto.response.book;

import com.example.book_inventory.model.Book.BookGenre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {

    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private Double price;
    private String description;
    private BookGenre genre;

    // inventory visibility
    private Boolean outOfStock;
    private String stockMessage;
}
