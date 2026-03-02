package com.example.book_inventory.dto.response.order;

import com.example.book_inventory.model.Cart.CartItem;
import com.example.book_inventory.model.Order.OrderStatus;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderResponse {
    private String orderId;
    private String userId;

    private List<CartItem> items;  // using same CartItem structure

    private double totalPrice;
    private OrderStatus status;

    private Date createdAt;
}
