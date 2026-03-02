package com.example.book_inventory.service.review;

import com.example.book_inventory.dto.request.review.CreateReviewRequest;
import com.example.book_inventory.dto.response.review.BookRatingResponse;
import com.example.book_inventory.dto.response.review.ReviewResponse;

import java.util.List;

public interface ReviewService {

    ReviewResponse addReview(String bookId, CreateReviewRequest request);

    List<ReviewResponse> getReviewsForBook(String bookId);

    BookRatingResponse getRatingForBook(String bookId);

    void deleteReview(String reviewId);
}
