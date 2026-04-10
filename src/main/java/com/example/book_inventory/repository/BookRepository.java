package com.example.book_inventory.repository;

import com.example.book_inventory.model.Book.BookDocument;
import com.example.book_inventory.model.Book.BookGenre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends MongoRepository<BookDocument, String> {
Optional<BookDocument> findByBookId(String bookId);

List<BookDocument> findByTitleContainingIgnoreCase(String title);

List<BookDocument> findByAuthorContainingIgnoreCase(String author);

Optional<BookDocument> findByIsbn(String isbn);

Page<BookDocument> findAll(Pageable pageable);

Optional<BookDocument> findByBookIdAndActiveTrue(String bookId);

Page<BookDocument> findByGenre(BookGenre genre, Pageable pageable);

@Query("{ '$or': [ { 'title': { $regex: ?0, $options: 'i' } }, { 'author': { $regex: ?0, $options: 'i' } }, { 'genre': { $regex: ?0, $options: 'i' } } ] }")
Page<BookDocument> searchBooks(String query, Pageable pageable);

}
