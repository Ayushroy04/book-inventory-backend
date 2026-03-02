package com.example.book_inventory.service.Checkout;

import com.example.book_inventory.dto.request.checkout.CheckoutRequest;
import com.example.book_inventory.dto.response.checkout.CheckoutResponse;
import com.example.book_inventory.exception.Cart.CartItemNotFoundException;
import com.example.book_inventory.exception.domain.InsufficientStockException;
import com.example.book_inventory.exception.User.UserNotFoundException;
import com.example.book_inventory.exception.book.BookNotFoundException;
import com.example.book_inventory.model.Book.BookDocument;
import com.example.book_inventory.model.Cart.CartDocument;
import com.example.book_inventory.model.Cart.CartItem;
import com.example.book_inventory.model.Cart.CartStatus;
import com.example.book_inventory.model.Order.OrderDocument;
import com.example.book_inventory.model.Order.OrderStatus;
import com.example.book_inventory.repository.BookRepository;
import com.example.book_inventory.repository.CartRepository;
import com.example.book_inventory.repository.OrderRepository;
import com.example.book_inventory.repository.UserRepository;
import com.example.book_inventory.service.Checkout.CheckoutService;
import com.example.book_inventory.service.EmailService.EmailService;
import com.example.book_inventory.model.User.UserDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

        private final UserRepository userRepository;
        private final CartRepository cartRepository;
        private final BookRepository bookRepository;
        private final OrderRepository orderRepository;
        private final EmailService emailService;

        @Override
        @Transactional
        public CheckoutResponse checkout(CheckoutRequest request) {

                // 1) Validate the user exists
                userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new UserNotFoundException(
                                                "User not found with id: " + request.getUserId()));

                // 2) Fetch ACTIVE cart
                CartDocument cart = cartRepository
                                .findByUserIdAndStatus(request.getUserId(), CartStatus.ACTIVE)
                                .orElseThrow(() -> new CartItemNotFoundException(
                                                "Active cart not found for userId: " + request.getUserId()));

                if (cart.getItems() == null || cart.getItems().isEmpty()) {
                        throw new CartItemNotFoundException("Cart is empty for userId: " + request.getUserId());
                }

                // 3) Validate cart items (ONLY validate)
                for (CartItem item : cart.getItems()) {

                        BookDocument book = bookRepository.findByBookId(item.getBookId())
                                        .orElseThrow(() -> new BookNotFoundException(
                                                        "Checkout failed. Book with ID '" + item.getBookId()
                                                                        + "' not found in inventory."));

                        if (item.getQuantity() <= 0) {
                                throw new IllegalArgumentException(
                                                "Checkout failed. Invalid quantity (" + item.getQuantity()
                                                                + ") for book '" + item.getTitle() + "'.");
                        }

                        if (book.getAvailableStock() < item.getQuantity()) {
                                throw new InsufficientStockException(
                                                "Checkout failed. Insufficient stock for '" + book.getTitle()
                                                                + "'. Available: " + book.getAvailableStock()
                                                                + ", requested: " + item.getQuantity());
                        }

                        if (Boolean.FALSE.equals(book.getActive())) {
                                throw new BookNotFoundException("Checkout failed. Book '" + book.getTitle()
                                                + "' is currently deactivated.");
                        }
                }

                // 4) Calculate total price (separate step)
                double totalPrice = 0;

                for (CartItem item : cart.getItems()) {
                        BookDocument book = bookRepository.findByBookId(item.getBookId())
                                        .orElseThrow(() -> new BookNotFoundException(
                                                        "Checkout failed. Book with ID '" + item.getBookId()
                                                                        + "' not found."));

                        totalPrice += book.getPrice() * item.getQuantity();
                }

                // 5) Reduce stock (separate step)
                for (CartItem item : cart.getItems()) {
                        BookDocument book = bookRepository.findByBookId(item.getBookId())
                                        .orElseThrow(() -> new BookNotFoundException(
                                                        "Checkout failed. Book with ID '" + item.getBookId()
                                                                        + "' not found."));

                        book.setAvailableStock(book.getAvailableStock() - item.getQuantity());
                        bookRepository.save(book);
                }

                // 6) Create order and save in DB
                OrderDocument orderDocument = new OrderDocument();
                orderDocument.setUserId(request.getUserId());
                orderDocument.setItems(cart.getItems());
                orderDocument.setTotalPrice(totalPrice);
                orderDocument.setStatus(OrderStatus.PLACED);

                OrderDocument savedOrder = orderRepository.save(orderDocument);

                // 7) Clear the cart after order created
                cart.getItems().clear();
                cart.setStatus(CartStatus.CHECKED_OUT);
                cartRepository.save(cart);

                // 8) Send confirmation email
                try {
                        UserDocument user = userRepository.findById(request.getUserId()).orElse(null);
                        if (user != null && user.getEmail() != null) {
                                emailService.sendOrderConfirmationEmail(user.getEmail(), savedOrder.getOrderId(),
                                                savedOrder.getTotalPrice());
                        }
                } catch (Exception e) {
                        // Log error but don't fail the checkout if email fails
                        org.slf4j.LoggerFactory.getLogger(CheckoutServiceImpl.class)
                                        .error("Failed to send order confirmation email: {}", e.getMessage());
                }

                // 9) Response
                CheckoutResponse response = new CheckoutResponse();
                response.setOrderId(savedOrder.getOrderId());
                response.setTotalPrice(savedOrder.getTotalPrice());
                response.setStatus(savedOrder.getStatus());
                response.setMessage("Checkout successful. Order placed.");

                return response;
        }
}
