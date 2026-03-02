package com.example.book_inventory.dto.request.checkout;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {

    @NotBlank(message = "UserId is required")
    private String userId;
}
