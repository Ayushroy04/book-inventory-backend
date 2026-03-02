package com.example.book_inventory.repository;

import com.example.book_inventory.model.Wishlist.WishlistDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends MongoRepository<WishlistDocument, String> {

    Optional<WishlistDocument> findByUserId(String userId);
}
