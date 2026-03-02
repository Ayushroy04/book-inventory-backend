package com.example.book_inventory.service.RazorpayService;

import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Thin wrapper around RazorpayClient for any future helper methods.
 * The actual RazorpayClient bean is configured in RazorpayConfig.
 */
@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final RazorpayClient razorpayClient;
}
