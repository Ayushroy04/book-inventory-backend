package com.example.book_inventory.controller;

import com.example.book_inventory.dto.request.review.CreateReviewRequest;
import com.example.book_inventory.dto.response.review.BookRatingResponse;
import com.example.book_inventory.dto.response.review.ReviewResponse;
import com.example.book_inventory.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // POST /books/{bookId}/reviews — Submit a review
    @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @PathVariable String bookId,
            @Valid @RequestBody CreateReviewRequest request) {
        return new ResponseEntity<>(reviewService.addReview(bookId, request), HttpStatus.CREATED);
    }

    // GET /books/{bookId}/reviews — Get all reviews for a book
    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable String bookId) {
        return new ResponseEntity<>(reviewService.getReviewsForBook(bookId), HttpStatus.OK);
    }

    // GET /books/{bookId}/rating — Get average rating for a book
    @GetMapping("/books/{bookId}/rating")
    public ResponseEntity<BookRatingResponse> getRating(@PathVariable String bookId) {
        return new ResponseEntity<>(reviewService.getRatingForBook(bookId), HttpStatus.OK);
    }

    // DELETE /reviews/{reviewId} — Delete your own review
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
