package com.example.book_inventory.service.order;

import com.example.book_inventory.dto.response.PageResponse;
import com.example.book_inventory.dto.response.order.OrderResponse;
import java.util.List;

import com.example.book_inventory.dto.request.order.PlaceOrderRequest;
import com.example.book_inventory.model.Order.OrderStatus;

public interface OrderService {

    PageResponse<OrderResponse> getAllOrders(int page, int size);

    OrderResponse getOrderById(String orderId);

    List<OrderResponse> getOrdersByUserId(String userId);

    OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus);

    OrderResponse placeOrder(String userId, PlaceOrderRequest request);

}
