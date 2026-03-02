package com.example.book_inventory.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentVerifyResponse {
    private String orderId;
    private double totalPrice;
    private String message;
}
