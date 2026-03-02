package com.example.book_inventory.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddToCartRequest {

    @NotBlank
    private String bookId;

    @Min(value = 1, message = "Quantity must be >= 1")
    private int quantity;
}
