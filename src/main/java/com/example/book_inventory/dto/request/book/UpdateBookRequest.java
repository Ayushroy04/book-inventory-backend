package com.example.book_inventory.dto.request.book;

import com.example.book_inventory.model.Book.BookGenre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, message = "Title must be at least 3 characters")
    private String title;

    @NotBlank(message = "Book ID is required")
    private String bookId;

    @NotBlank(message = "Author is required")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 17, message = "ISBN must be between 10 and 17 characters")
    private String isbn;

    private String publisher;

    private Integer publicationYear;

    @jakarta.validation.constraints.NotNull(message = "Price is required")
    @jakarta.validation.constraints.Min(value = 0, message = "Price cannot be negative")
    private Double price;

    private String language;
    private String description;
    private BookGenre genre;

}
