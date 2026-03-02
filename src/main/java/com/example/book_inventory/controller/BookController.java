package com.example.book_inventory.controller;

import com.example.book_inventory.dto.request.book.CreateBookRequest;
import com.example.book_inventory.dto.request.book.UpdateBookRequest;
import com.example.book_inventory.dto.response.PageResponse;
import com.example.book_inventory.dto.response.book.BookResponse;
import com.example.book_inventory.dto.response.book.UpdateBookResponse;
import com.example.book_inventory.model.Book.BookGenre;
import com.example.book_inventory.service.book.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody CreateBookRequest request) {
        BookResponse savedBook = bookService.createBook(request);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BookGenre genre,
            @RequestParam(required = false) String search) {
        return new ResponseEntity<>(bookService.getAllBooks(page, size, genre, search), HttpStatus.OK);
    }

    @GetMapping("/genres")
    public ResponseEntity<List<BookGenre>> getGenres() {
        return new ResponseEntity<>(bookService.getAvailableGenres(), HttpStatus.OK);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<UpdateBookResponse> updateBook(@PathVariable String bookId,
            @Valid @RequestBody UpdateBookRequest request) {
        UpdateBookResponse response = bookService.updateBook(bookId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable String bookId) {
        bookService.deactivateBook(bookId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookByBookId(@PathVariable String bookId) {
        return new ResponseEntity<>(bookService.getBookByBookId(bookId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {
        return new ResponseEntity<>(bookService.searchBook(title, author), HttpStatus.OK);
    }

    @PostMapping("/{bookId}/stock/reduce")
    public ResponseEntity<Void> reduceBookStock(@PathVariable String bookId, @RequestParam int quantity) {
        bookService.reduceBookStock(bookId, quantity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{bookId}/stock/add")
    public ResponseEntity<Void> addBookStock(@PathVariable String bookId, @RequestParam int quantity) {
        bookService.addBookStock(bookId, quantity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
