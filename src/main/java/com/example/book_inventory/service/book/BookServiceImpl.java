package com.example.book_inventory.service.book;

import com.example.book_inventory.dto.request.book.CreateBookRequest;
import com.example.book_inventory.dto.request.book.UpdateBookRequest;
import com.example.book_inventory.dto.response.PageResponse;
import com.example.book_inventory.dto.response.book.BookResponse;
import com.example.book_inventory.dto.response.book.UpdateBookResponse;
import com.example.book_inventory.exception.book.BookNotFoundException;
import com.example.book_inventory.exception.book.DuplicateBookException;
import com.example.book_inventory.exception.domain.InsufficientStockException;
import com.example.book_inventory.model.Book.BookDocument;
import com.example.book_inventory.model.Book.BookGenre;
import com.example.book_inventory.repository.BookRepository;
import com.example.book_inventory.utils.InventoryVisibilityHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final InventoryVisibilityHelper inventoryVisibilityHelper;

    @Override
    public BookResponse createBook(CreateBookRequest request) {

        // Check for duplicate book based on ISBN (industry standard)
        bookRepository.findByIsbn(request.getIsbn())
                .ifPresent(book -> {
                    if (Boolean.TRUE.equals(book.getActive())) {
                        throw new DuplicateBookException(
                                "Book already exists with ISBN: " + request.getIsbn());
                    }
                });

        BookDocument bookDocument = new BookDocument();

        bookDocument.setBookId(java.util.UUID.randomUUID().toString());
        bookDocument.setIsbn(request.getIsbn());
        bookDocument.setTitle(request.getTitle());
        bookDocument.setAuthor(request.getAuthor());
        bookDocument.setPublisher(request.getPublisher());
        bookDocument.setPublicationYear(request.getPublicationYear());
        bookDocument.setPrice(request.getPrice());
        bookDocument.setLanguage(request.getLanguage());
        bookDocument.setGenre(request.getGenre());

        // inventory system
        bookDocument.setTotalStock(request.getTotalStock());
        bookDocument.setAvailableStock(request.getTotalStock());
        bookDocument.setActive(true);

        BookDocument savedBook = bookRepository.save(bookDocument);

        // convert back to Dto
        BookResponse response = new BookResponse();

        response.setBookId(savedBook.getBookId());
        response.setIsbn(savedBook.getIsbn());
        response.setTitle(savedBook.getTitle());
        response.setAuthor(savedBook.getAuthor());
        response.setPrice(savedBook.getPrice());
        response.setDescription(savedBook.getDescription());
        response.setGenre(savedBook.getGenre());

        // inventory visibility
        inventoryVisibilityHelper.apply(savedBook, response);

        return response;
    }

    @Override
    public PageResponse<BookResponse> getAllBooks(int page, int size, BookGenre genre, String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BookDocument> bookPage;

        if (search != null && !search.trim().isEmpty()) {
            bookPage = bookRepository.searchBooks(search.trim(), pageable);
        } else if (genre != null) {
            bookPage = bookRepository.findByGenre(genre, pageable);
        } else {
            bookPage = bookRepository.findAll(pageable);
        }

        List<BookResponse> content = bookPage.getContent().stream()
                .filter(book -> book.getActive() == null || book.getActive())
                .map(book -> {
                    BookResponse response = new BookResponse();
                    response.setBookId(book.getBookId());
                    response.setIsbn(book.getIsbn());
                    response.setTitle(book.getTitle());
                    response.setAuthor(book.getAuthor());
                    response.setPrice(book.getPrice());
                    response.setDescription(book.getDescription());
                    response.setGenre(book.getGenre());
                    inventoryVisibilityHelper.apply(book, response);
                    return response;
                })
                .collect(Collectors.toList());

        return PageResponse.<BookResponse>builder()
                .content(content)
                .pageNumber(bookPage.getNumber())
                .pageSize(bookPage.getSize())
                .totalElements(bookPage.getTotalElements())
                .totalPages(bookPage.getTotalPages())
                .last(bookPage.isLast())
                .build();
    }

    @Override
    public List<BookGenre> getAvailableGenres() {
        return Arrays.asList(BookGenre.values());
    }

    @Override
    public UpdateBookResponse updateBook(String bookId, UpdateBookRequest request) {
        BookDocument bookDocument = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new BookNotFoundException("Unable to update. Book not found with ID: " + bookId));

        if (Boolean.FALSE.equals(bookDocument.getActive())) {
            throw new BookNotFoundException(
                    "Cannot update details for book '" + bookDocument.getTitle() + "' (ID: " + bookId
                            + ") because it has been deactivated.");
        }

        bookDocument.setTitle(request.getTitle());
        bookDocument.setAuthor(request.getAuthor());
        bookDocument.setPublisher(request.getPublisher());
        bookDocument.setPublicationYear(request.getPublicationYear());
        bookDocument.setPrice(request.getPrice());
        bookDocument.setLanguage(request.getLanguage());
        bookDocument.setGenre(request.getGenre());

        BookDocument updatedBook = bookRepository.save(bookDocument);
        UpdateBookResponse response = new UpdateBookResponse();

        response.setBookId(updatedBook.getBookId());
        response.setTitle(updatedBook.getTitle());
        response.setAuthor(updatedBook.getAuthor());
        response.setPrice(updatedBook.getPrice());

        return response;
    }

    @Override
    public void deactivateBook(String bookId) {
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!authentication.equals("admin")) {
            throw new RuntimeException("Unauthorized: only Admin can deactivate books.");
        }

        BookDocument book = bookRepository.findByBookId(bookId)
                .orElseThrow(() -> new BookNotFoundException(
                        "Cannot deactivate. Book not found with ID: " + bookId));

        // idempotent behavior (already inactive → do nothing)
        if (Boolean.FALSE.equals(book.getActive())) {
            return;
        }
        // SOFT DELETE
        book.setActive(false);
        bookRepository.save(book);
    }

    @Override
    public BookResponse getBookByBookId(String bookId) {
        BookDocument bookDocument = bookRepository.findByBookId(bookId)
                .orElseThrow(
                        () -> new BookNotFoundException("Book could not be retrieved. Not found with ID: " + bookId));

        if (Boolean.FALSE.equals(bookDocument.getActive())) {
            throw new BookNotFoundException(
                    "Book '" + bookDocument.getTitle() + "' (ID: " + bookId + ") is currently inactive.");
        }
        BookResponse response = new BookResponse();

        response.setBookId(bookDocument.getBookId());
        response.setIsbn(bookDocument.getIsbn());
        response.setTitle(bookDocument.getTitle());
        response.setAuthor(bookDocument.getAuthor());
        response.setPrice(bookDocument.getPrice());
        response.setDescription(bookDocument.getDescription());
        response.setGenre(bookDocument.getGenre());

        inventoryVisibilityHelper.apply(bookDocument, response);

        return response;
    }

    @Override
    public List<BookResponse> searchBook(String title, String author) {
        List<BookDocument> books;
        if (title != null && !title.isEmpty()) {
            books = bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null && !author.isEmpty()) {
            books = bookRepository.findByAuthorContainingIgnoreCase(author);
        } else {
            books = bookRepository.findAll();
        }

        return books.stream()
                .filter(book -> book.getActive() == null || book.getActive())
                .map(book -> {
                    BookResponse response = new BookResponse();
                    response.setBookId(book.getBookId());
                    response.setIsbn(book.getIsbn());
                    response.setTitle(book.getTitle());
                    response.setAuthor(book.getAuthor());
                    response.setPrice(book.getPrice());
                    response.setDescription(book.getDescription());
                    response.setGenre(book.getGenre());
                    inventoryVisibilityHelper.apply(book, response);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void reduceBookStock(String bookId, int quantity) {

        // 1️⃣ Validate quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // 2️⃣ Fetch book
        BookDocument book = bookRepository.findByBookId(bookId)
                .orElseThrow(
                        () -> new BookNotFoundException("Stock reduction failed. Book not found with ID: " + bookId));

        // 3️⃣ Check the active status
        if (Boolean.FALSE.equals(book.getActive())) {
            throw new BookNotFoundException(
                    "Cannot borrow book '" + book.getTitle() + "'. It is currently deactivated.");
        }

        // 4️⃣ Check stock BEFORE reducing
        Integer availableStockObj = book.getAvailableStock();
        int availableStock = (availableStockObj == null) ? 0 : availableStockObj;

        if (availableStock < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock. Available: " + availableStock + ", Requested: " + quantity);
        }

        // 5️⃣ Reduce stock ONCE
        book.setAvailableStock(availableStock - quantity);

        // 6️⃣ Save
        bookRepository.save(book);
    }

    @Override
    public void addBookStock(String bookId, int quantity) {

        // 1) Validate quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // 2) Fetch book
        BookDocument book = bookRepository.findByBookId(bookId)
                .orElseThrow(
                        () -> new BookNotFoundException("Stock addition failed. Book not found with ID: " + bookId));

        // 3) Active check (soft delete enforcement)
        if (Boolean.FALSE.equals(book.getActive())) {
            throw new BookNotFoundException(
                    "Cannot add stock. Book '" + book.getTitle() + "' (ID: " + bookId + ") is deactivated.");
        }

        // 4) Null-safe stock read
        Integer totalStockObj = book.getTotalStock();
        Integer availableStockObj = book.getAvailableStock();

        int totalStock = (totalStockObj == null) ? 0 : totalStockObj;
        int availableStock = (availableStockObj == null) ? 0 : availableStockObj;

        // 5) Add stock
        book.setTotalStock(totalStock + quantity);
        book.setAvailableStock(availableStock + quantity);

        // 6) Save
        bookRepository.save(book);
    }
}
