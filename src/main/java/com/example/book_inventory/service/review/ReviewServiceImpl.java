package com.example.book_inventory.service.review;

import com.example.book_inventory.config.UserPrincipal;
import com.example.book_inventory.dto.request.review.CreateReviewRequest;
import com.example.book_inventory.dto.response.review.BookRatingResponse;
import com.example.book_inventory.dto.response.review.ReviewResponse;
import com.example.book_inventory.exception.book.BookNotFoundException;
import com.example.book_inventory.exception.review.DuplicateReviewException;
import com.example.book_inventory.exception.review.ReviewNotFoundException;
import com.example.book_inventory.model.Review.ReviewDocument;
import com.example.book_inventory.repository.BookRepository;
import com.example.book_inventory.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    @Override
    public ReviewResponse addReview(String bookId, CreateReviewRequest request) {

        // 1. Verify the book exists and is active
        bookRepository.findByBookId(bookId)
                .filter(book -> Boolean.TRUE.equals(book.getActive()))
                .orElseThrow(() -> new BookNotFoundException(
                        "Cannot review. Book not found with ID: " + bookId));

        // 2. Get the currently logged-in user
        UserPrincipal principal = getCurrentUser();

        // 3. Prevent duplicate review — one review per user per book
        if (reviewRepository.existsByBookIdAndUserId(bookId, principal.getUserId())) {
            throw new DuplicateReviewException(
                    "You have already reviewed this book. Delete your existing review to submit a new one.");
        }

        // 4. Build and save the review
        ReviewDocument review = new ReviewDocument();
        review.setReviewId(UUID.randomUUID().toString());
        review.setBookId(bookId);
        review.setUserId(principal.getUserId());
        review.setUsername(principal.getUsername()); // email used as display name
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        ReviewDocument saved = reviewRepository.save(review);

        return toResponse(saved);
    }

    @Override
    public List<ReviewResponse> getReviewsForBook(String bookId) {
        return reviewRepository.findByBookId(bookId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookRatingResponse getRatingForBook(String bookId) {
        List<ReviewDocument> reviews = reviewRepository.findByBookId(bookId);

        int total = reviews.size();
        double average = total == 0 ? 0.0
                : reviews.stream()
                        .mapToInt(ReviewDocument::getRating)
                        .average()
                        .orElse(0.0);

        // Round to 1 decimal place
        double rounded = Math.round(average * 10.0) / 10.0;

        return BookRatingResponse.builder()
                .bookId(bookId)
                .averageRating(rounded)
                .totalReviews(total)
                .build();
    }

    @Override
    public void deleteReview(String reviewId) {
        UserPrincipal principal = getCurrentUser();

        ReviewDocument review = reviewRepository.findByReviewId(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(
                        "Review not found with ID: " + reviewId));

        // Only the owner can delete their review
        if (!review.getUserId().equals(principal.getUserId())) {
            throw new RuntimeException("Unauthorized: You can only delete your own reviews.");
        }

        reviewRepository.delete(review);
    }

    // ── Helper Methods ──────────────────────────────────────────────────────────

    private ReviewResponse toResponse(ReviewDocument review) {
        return ReviewResponse.builder()
                .reviewId(review.getReviewId())
                .bookId(review.getBookId())
                .userId(review.getUserId())
                .username(review.getUsername())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    private UserPrincipal getCurrentUser() {
        return (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
