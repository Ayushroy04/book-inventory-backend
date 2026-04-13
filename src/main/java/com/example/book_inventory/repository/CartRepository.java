package com.example.book_inventory.repository;

import com.example.book_inventory.model.Cart.CartDocument;
import com.example.book_inventory.model.Cart.CartStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends MongoRepository<CartDocument, String> {

    //Optional <CartDocument> findByUserId(String userId);

    List<CartDocument> findByUserIdAndStatus(String userId, CartStatus status);

}
