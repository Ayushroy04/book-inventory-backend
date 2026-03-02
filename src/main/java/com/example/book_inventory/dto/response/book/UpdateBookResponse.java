package com.example.book_inventory.dto.response.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookResponse {
    private String bookId;
    private String title;
    private String author;
    private Double price;

    // inventory visibility
    private Boolean outOfStock;
    private String stockMessage;
}

