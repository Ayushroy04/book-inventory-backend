package com.example.book_inventory.repository;

import com.example.book_inventory.model.Review.ReviewDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<ReviewDocument, String> {

    List<ReviewDocument> findByBookId(String bookId);

    Optional<ReviewDocument> findByBookIdAndUserId(String bookId, String userId);

    Optional<ReviewDocument> findByReviewId(String reviewId);

    boolean existsByBookIdAndUserId(String bookId, String userId);
}
