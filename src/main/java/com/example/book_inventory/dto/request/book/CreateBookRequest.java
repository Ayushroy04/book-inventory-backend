package com.example.book_inventory.dto.request.book;

import com.example.book_inventory.model.Book.BookGenre;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookRequest {

    @NotBlank
    @Size(min = 3, message = "Title must be at least 3 characters")
    private String title;

    @NotBlank(message = "Author is Required")
    private String author;

    @NotBlank(message = "ISBN is required")
    @Size(min = 10, max = 17, message = "ISBN must be between 10 and 17 characters")
    private String isbn; // ISBN-10 or ISBN-13

    private String publisher;
    private Integer publicationYear;
    private double price;
    private String language;
    private String description;
    private BookGenre genre;

    @NotNull
    @Min(1)
    private Integer totalStock;
}
