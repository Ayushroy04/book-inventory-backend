package com.example.book_inventory.service.book;

import com.example.book_inventory.dto.request.book.CreateBookRequest;
import com.example.book_inventory.dto.request.book.UpdateBookRequest;
import com.example.book_inventory.dto.response.PageResponse;
import com.example.book_inventory.dto.response.book.BookResponse;
import com.example.book_inventory.dto.response.book.UpdateBookResponse;
import com.example.book_inventory.model.Book.BookGenre;

import java.util.List;

public interface BookService {
    BookResponse createBook(CreateBookRequest request);

    PageResponse<BookResponse> getAllBooks(int page, int size, BookGenre genre, String search);

    UpdateBookResponse updateBook(String bookId, UpdateBookRequest request);

    // void deleteBook(String bookId);

    BookResponse getBookByBookId(String bookId);

    List<BookResponse> searchBook(String title, String author);

    void reduceBookStock(String bookId, int quantity);

    void deactivateBook(String bookId);

    void addBookStock(String bookId, int quantity);

    List<BookGenre> getAvailableGenres();

}
