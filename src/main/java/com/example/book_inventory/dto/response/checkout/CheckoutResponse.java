package com.example.book_inventory.dto.response.checkout;
import com.example.book_inventory.model.Order.OrderStatus;

import lombok.Data;

@Data
public class CheckoutResponse {

    private String orderId;
    private double totalPrice;
    private OrderStatus status;
    private String message;
}
