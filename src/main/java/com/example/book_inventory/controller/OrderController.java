package com.example.book_inventory.controller;

import com.example.book_inventory.config.UserPrincipal;
import com.example.book_inventory.dto.request.order.PlaceOrderRequest;
import com.example.book_inventory.dto.response.PageResponse;
import com.example.book_inventory.dto.response.order.OrderResponse;
import com.example.book_inventory.model.Order.OrderStatus;
import com.example.book_inventory.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /orders — any authenticated user can place an order directly
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PlaceOrderRequest request) {
        return new ResponseEntity<>(orderService.placeOrder(principal.getUserId(), request), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(orderService.getAllOrders(page, size), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrdersByUserId(@PathVariable String userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // PATCH /orders/{orderId}/status?status=SHIPPED — Admin only, triggers email
    // automatically
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status) {
        return new ResponseEntity<>(orderService.updateOrderStatus(orderId, status), HttpStatus.OK);
    }
}
