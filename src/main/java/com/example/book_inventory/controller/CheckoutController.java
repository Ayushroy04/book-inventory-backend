package com.example.book_inventory.controller;

import com.example.book_inventory.dto.request.checkout.CheckoutRequest;
import com.example.book_inventory.dto.response.checkout.CheckoutResponse;
import com.example.book_inventory.service.Checkout.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutResponse> createCheckout(@Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = checkoutService.checkout(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
