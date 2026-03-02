package com.example.book_inventory.repository;

import com.example.book_inventory.model.Cart.CartDocument;
import com.example.book_inventory.model.Cart.CartStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends MongoRepository<CartDocument, String> {

    //Optional <CartDocument> findByUserId(String userId);

    Optional<CartDocument> findByUserIdAndStatus(String userId, CartStatus status);

}
