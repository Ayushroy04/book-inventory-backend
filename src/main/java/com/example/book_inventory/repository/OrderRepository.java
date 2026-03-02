package com.example.book_inventory.repository;

import com.example.book_inventory.model.Order.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<OrderDocument, String> {

    List<OrderDocument> findByUserId(String userId);

}
