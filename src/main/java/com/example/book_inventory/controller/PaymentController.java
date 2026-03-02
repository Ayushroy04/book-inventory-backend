package com.example.book_inventory.controller;

import com.example.book_inventory.config.UserPrincipal;
import com.example.book_inventory.dto.request.checkout.CheckoutRequest;
import com.example.book_inventory.dto.request.order.PlaceOrderRequest;
import com.example.book_inventory.dto.request.payment.PaymentOrderRequest;
import com.example.book_inventory.dto.request.payment.PaymentVerifyRequest;
import com.example.book_inventory.dto.response.checkout.CheckoutResponse;
import com.example.book_inventory.dto.response.order.OrderResponse;
import com.example.book_inventory.dto.response.payment.PaymentOrderResponse;
import com.example.book_inventory.dto.response.payment.PaymentVerifyResponse;
import com.example.book_inventory.service.Checkout.CheckoutService;
import com.example.book_inventory.service.order.OrderService;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayClient razorpayClient;
    private final CheckoutService checkoutService;
    private final OrderService orderService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    /**
     * Step 1 — Frontend calls this to create a Razorpay order.
     * Returns { razorpayOrderId, amount, currency, keyId } which the JS SDK needs.
     */
    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @Valid @RequestBody PaymentOrderRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        // Security: the user can only create orders for themselves
        if (!request.getUserId().equals(principal.getUserId())) {
            throw new AccessDeniedException("You can only create orders for your own account.");
        }

        try {
            // receipt must be ≤ 40 chars — use last 8 chars of userId + epoch in seconds
            String uid8 = request.getUserId().substring(Math.max(0, request.getUserId().length() - 8));
            JSONObject orderOptions = new JSONObject();
            orderOptions.put("amount", request.getAmount());
            orderOptions.put("currency", "INR");
            orderOptions.put("receipt", "rcpt_" + uid8 + "_" + (System.currentTimeMillis() / 1000));

            orderOptions.put("payment_capture", 1); // auto-capture

            Order razorpayOrder = razorpayClient.orders.create(orderOptions);

            PaymentOrderResponse response = new PaymentOrderResponse(
                    razorpayOrder.get("id").toString(),
                    ((Number) razorpayOrder.get("amount")).longValue(),
                    razorpayOrder.get("currency").toString(),
                    razorpayKeyId);

            return ResponseEntity.ok(response);

        } catch (RazorpayException e) {
            log.error("Failed to create Razorpay order for userId={}: {}", request.getUserId(), e.getMessage());
            throw new RuntimeException("Payment gateway error: " + e.getMessage());
        }
    }

    /**
     * Step 2 — Frontend calls this AFTER the user completes payment in the Razorpay
     * popup.
     * Verifies the HMAC-SHA256 signature, then triggers checkout (creates DB order
     * + clears cart).
     */
    @PostMapping("/verify")
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(
            @Valid @RequestBody PaymentVerifyRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        // Security: the user can only verify their own payment
        if (!request.getUserId().equals(principal.getUserId())) {
            throw new AccessDeniedException("You can only verify your own payment.");
        }

        // Verify signature: HMAC_SHA256(razorpayOrderId + "|" + razorpayPaymentId,
        // secret)
        if (!isSignatureValid(request.getRazorpayOrderId(), request.getRazorpayPaymentId(),
                request.getRazorpaySignature())) {
            log.warn("Invalid Razorpay signature for userId={}", request.getUserId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PaymentVerifyResponse(null, 0, "Payment verification failed: invalid signature."));
        }

        // Signature is valid → complete the checkout (deduct stock, create order, clear
        // cart)
        String orderId;
        double totalPrice;

        if (request.getBookId() != null && !request.getBookId().isEmpty()) {
            // Direct purchase (Buy Now)
            PlaceOrderRequest directOrder = new PlaceOrderRequest();
            directOrder.setBookId(request.getBookId());
            directOrder.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);

            OrderResponse order = orderService.placeOrder(request.getUserId(), directOrder);
            orderId = order.getOrderId();
            totalPrice = order.getTotalPrice();
        } else {
            // Cart checkout
            CheckoutRequest checkoutRequest = new CheckoutRequest();
            checkoutRequest.setUserId(request.getUserId());
            CheckoutResponse checkout = checkoutService.checkout(checkoutRequest);
            orderId = checkout.getOrderId();
            totalPrice = checkout.getTotalPrice();
        }

        log.info("Payment verified and order created: orderId={}, userId={}", orderId,
                request.getUserId());

        return ResponseEntity.ok(new PaymentVerifyResponse(
                orderId,
                totalPrice,
                "Payment successful! Order placed."));
    }

    // ─── Signature Verification ───────────────────────────────────────────────

    private boolean isSignatureValid(String razorpayOrderId, String razorpayPaymentId, String signature) {
        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            // Convert to hex string manually
            StringBuilder sb = new StringBuilder();
            for (byte b : hash)
                sb.append(String.format("%02x", b));
            return sb.toString().equals(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Signature verification error: {}", e.getMessage());
            return false;
        }
    }
}
