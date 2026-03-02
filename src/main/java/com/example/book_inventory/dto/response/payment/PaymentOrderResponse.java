package com.example.book_inventory.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentOrderResponse {
    private String razorpayOrderId;
    private long amount; // in paise
    private String currency;
    private String keyId; // sent to frontend so it can open the Razorpay popup
}
