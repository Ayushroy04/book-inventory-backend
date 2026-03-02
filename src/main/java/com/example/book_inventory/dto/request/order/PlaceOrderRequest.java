package com.example.book_inventory.dto.request.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlaceOrderRequest {

    @NotBlank(message = "Book ID is required")
    private String bookId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
