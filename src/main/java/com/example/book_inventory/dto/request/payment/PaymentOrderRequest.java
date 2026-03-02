package com.example.book_inventory.dto.request.payment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentOrderRequest {

    @NotBlank(message = "userId is required")
    private String userId;

    @Min(value = 1, message = "Amount must be > 0")
    private long amount; // in paise (₹1 = 100 paise)
}
