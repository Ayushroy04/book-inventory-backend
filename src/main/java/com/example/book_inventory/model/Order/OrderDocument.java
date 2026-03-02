package com.example.book_inventory.model.Order;

import com.example.book_inventory.model.Cart.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Document(collection = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDocument {

    @Id
    private String orderId = UUID.randomUUID().toString();

    private String userId;
    private List<CartItem> items;

    private double totalPrice;
    private OrderStatus status;

    private Date createdAt = new Date();
}
