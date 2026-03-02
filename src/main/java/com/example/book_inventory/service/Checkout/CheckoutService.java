package com.example.book_inventory.service.Checkout;

import com.example.book_inventory.dto.request.checkout.CheckoutRequest;
import com.example.book_inventory.dto.response.checkout.CheckoutResponse;

public interface CheckoutService {

    CheckoutResponse checkout(CheckoutRequest checkoutRequest);
}
