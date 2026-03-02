package com.example.book_inventory.model.Book;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
@Data
@NoArgsConstructor
public class BookDocument {

    @Id
    private String id;

    private String bookId;
    private String isbn; // International Standard Book Number
    private String title;
    private String author;
    private String publisher;
    private Integer publicationYear;
    private Double price;
    private String language;
    private String description;
    private BookGenre genre;

    // Inventory fields
    private Integer totalStock;
    private Integer availableStock;
    private Boolean active;

    // Optimistic locking for concurrent updates
    @Version
    private Long version;
}
