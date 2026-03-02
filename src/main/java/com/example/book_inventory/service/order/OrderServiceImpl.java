package com.example.book_inventory.service.order;

import com.example.book_inventory.dto.request.order.PlaceOrderRequest;
import com.example.book_inventory.dto.response.PageResponse;
import com.example.book_inventory.dto.response.order.OrderResponse;
import com.example.book_inventory.exception.User.UserNotFoundException;
import com.example.book_inventory.exception.book.BookNotFoundException;
import com.example.book_inventory.exception.order.OrderNotFoundException;
import com.example.book_inventory.model.Cart.CartItem;
import com.example.book_inventory.model.Book.BookDocument;
import com.example.book_inventory.model.Order.OrderDocument;
import com.example.book_inventory.model.Order.OrderStatus;
import com.example.book_inventory.model.User.UserDocument;
import com.example.book_inventory.repository.BookRepository;
import com.example.book_inventory.repository.OrderRepository;
import com.example.book_inventory.repository.UserRepository;
import com.example.book_inventory.service.EmailService.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
        private final OrderRepository orderRepository;
        private final UserRepository userRepository;
        private final BookRepository bookRepository;
        private final EmailService emailService;

        @Override
        public PageResponse<OrderResponse> getAllOrders(int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                Page<OrderDocument> orderPage = orderRepository.findAll(pageable);

                List<OrderResponse> content = orderPage.getContent().stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());

                return PageResponse.<OrderResponse>builder()
                                .content(content)
                                .pageNumber(orderPage.getNumber())
                                .pageSize(orderPage.getSize())
                                .totalElements(orderPage.getTotalElements())
                                .totalPages(orderPage.getTotalPages())
                                .last(orderPage.isLast())
                                .build();
        }

        @Override
        public OrderResponse getOrderById(String orderId) {

                OrderDocument order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new OrderNotFoundException(
                                                "Retrieval failed. Order not found with ID: " + orderId));

                // Authorization: Verify order belongs to authenticated user (unless admin)
                org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                                .getContext().getAuthentication();

                if (authentication != null
                                && authentication
                                                .getPrincipal() instanceof com.example.book_inventory.config.UserPrincipal) {
                        com.example.book_inventory.config.UserPrincipal userPrincipal = (com.example.book_inventory.config.UserPrincipal) authentication
                                        .getPrincipal();

                        boolean isAdmin = userPrincipal.getAuthorities().stream()
                                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

                        if (!isAdmin && !order.getUserId().equals(userPrincipal.getUserId())) {
                                throw new org.springframework.security.access.AccessDeniedException(
                                                "You don't have permission to access this order");
                        }
                }

                return mapToResponse(order);
        }

        @Override
        public List<OrderResponse> getOrdersByUserId(String userId) {

                userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException(
                                                "Retrieval failed. User not found with ID: " + userId));

                List<OrderDocument> orders = orderRepository.findByUserId(userId);
                return orders.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Override
        public OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus) {
                OrderDocument order = orderRepository.findById(orderId)
                                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

                order.setStatus(newStatus);
                orderRepository.save(order);

                // Get user's email to send notification
                UserDocument user = userRepository.findById(order.getUserId())
                                .orElseThrow(() -> new UserNotFoundException(
                                                "User not found with ID: " + order.getUserId()));
                String email = user.getEmail();

                // Fire the right email based on status — runs async in background
                switch (newStatus) {
                        case PLACED -> emailService.sendOrderConfirmationEmail(email, orderId, order.getTotalPrice());
                        case SHIPPED -> emailService.sendShippingEmail(email, orderId);
                        case OUT_FOR_DELIVERY -> emailService.sendOutForDeliveryEmail(email, orderId);
                        case DELIVERED -> emailService.sendOrderDeliveryEmail(email, orderId);
                        case CANCELLED -> emailService.sendCancelOrderEmail(email, orderId);
                }

                return mapToResponse(order);
        }

        @Override
        public OrderResponse placeOrder(String userId, PlaceOrderRequest request) {
                // Validate user
                UserDocument user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

                // Validate book and stock
                BookDocument book = bookRepository.findByBookId(request.getBookId())
                                .orElseThrow(() -> new BookNotFoundException("Book not found: " + request.getBookId()));

                int qty = request.getQuantity();
                if (book.getAvailableStock() == null || book.getAvailableStock() < qty) {
                        throw new IllegalStateException("Insufficient stock for book: " + book.getTitle());
                }

                // Deduct stock
                book.setAvailableStock(book.getAvailableStock() - qty);
                bookRepository.save(book);

                // Build order item
                CartItem item = new CartItem();
                item.setBookId(book.getBookId());
                item.setTitle(book.getTitle());
                item.setAuthor(book.getAuthor());
                item.setQuantity(qty);

                item.setPriceAtAdding(book.getPrice());
                item.setSubTotal(book.getPrice() * qty);

                // Create and save order
                OrderDocument order = new OrderDocument();
                order.setUserId(userId);
                order.setItems(Collections.singletonList(item));
                order.setTotalPrice(item.getSubTotal());
                order.setStatus(OrderStatus.PLACED);
                OrderDocument saved = orderRepository.save(order);

                // Send confirmation email (async)
                emailService.sendOrderConfirmationEmail(user.getEmail(), saved.getOrderId(), saved.getTotalPrice());

                return mapToResponse(saved);
        }

        private OrderResponse mapToResponse(OrderDocument order) {
                OrderResponse response = new OrderResponse();
                response.setOrderId(order.getOrderId());
                response.setUserId(order.getUserId());
                response.setItems(order.getItems());
                response.setTotalPrice(order.getTotalPrice());
                response.setStatus(order.getStatus());
                response.setCreatedAt(order.getCreatedAt());
                return response;
        }
}
