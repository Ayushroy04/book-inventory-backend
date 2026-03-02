package com.example.book_inventory.model.Cart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "carts")
public class CartDocument {

    @Id
    private String id;

    private String userId;

    private CartStatus status = CartStatus.ACTIVE;

    private List<CartItem> items = new ArrayList<>();

    private Double totalAmount = 0.0;

    private Integer totalItems = 0;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();
}
