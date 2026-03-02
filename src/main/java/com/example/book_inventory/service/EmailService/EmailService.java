package com.example.book_inventory.service.EmailService;

public interface EmailService {

    void sendOrderConfirmationEmail(String email, String orderId, double totalPrice);

    void sendShippingEmail(String email, String orderID);

    void sendOutForDeliveryEmail(String email, String orderId);

    void sendOrderDeliveryEmail(String email, String orderId);

    void sendCancelOrderEmail(String email, String orderId);
}
