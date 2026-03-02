package com.example.book_inventory.dto.request.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentVerifyRequest {

    @NotBlank(message = "razorpayOrderId is required")
    private String razorpayOrderId;

    @NotBlank(message = "razorpayPaymentId is required")
    private String razorpayPaymentId;

    @NotBlank(message = "razorpaySignature is required")
    private String razorpaySignature;

    @NotBlank(message = "userId is required")
    private String userId;

    // Optional: for "Buy Now" (direct purchase)
    private String bookId;
    private Integer quantity;
}
