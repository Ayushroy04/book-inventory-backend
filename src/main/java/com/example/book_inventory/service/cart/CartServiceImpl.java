package com.example.book_inventory.service.cart;

import com.example.book_inventory.dto.request.cart.AddToCartRequest;
import com.example.book_inventory.dto.request.cart.UpdateCartItemRequest;
import com.example.book_inventory.dto.response.cart.CartResponse;
import com.example.book_inventory.exception.Cart.CartItemNotFoundException;
import com.example.book_inventory.exception.User.UserNotFoundException;
import com.example.book_inventory.exception.book.BookNotFoundException;
import com.example.book_inventory.exception.domain.InsufficientStockException;
import com.example.book_inventory.model.Book.BookDocument;
import com.example.book_inventory.model.Cart.CartDocument;
import com.example.book_inventory.model.Cart.CartItem;
import com.example.book_inventory.model.Cart.CartStatus;
import com.example.book_inventory.repository.BookRepository;
import com.example.book_inventory.repository.CartRepository;
import com.example.book_inventory.repository.UserRepository;
import com.example.book_inventory.utils.CartMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    private void validateUser(String userID) {
        if (!userRepository.existsById(userID)) {
            throw new UserNotFoundException("User not found: " + userID);
        }
    }

    // 1) Get the cart (ACTIVE cart only)
    @Override
    public CartResponse getCart(String userId) {
        validateUser(userId); // If user is fake it stops here
        CartDocument cart = getOrCreateActiveCart(userId);
        return CartMapper.toResponse(cart);
    }

    // 2) Add item to the cart
    @Override
    public CartResponse addToCart(String userId, AddToCartRequest request) {
        validateUser(userId);

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartDocument cart = getOrCreateActiveCart(userId);

        BookDocument book = bookRepository.findByBookId(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found with id: " + request.getBookId()));

        if (Boolean.FALSE.equals(book.getActive())) {
            throw new BookNotFoundException("Cannot add to cart. Book '" + book.getTitle() + "' (ID: "
                    + request.getBookId() + ") is currently deactivated.");
        }

        // STOCK VALIDATION
        // Calculate how many are already in the cart for this specific book
        int currentInCart = cart.getItems().stream()
                .filter(i -> i.getBookId().equals(request.getBookId()))
                .mapToInt(CartItem::getQuantity)
                .sum();

        // Calculate the new total the user is asking for
        int requestedTotal = currentInCart + request.getQuantity();

        // Compare against the database stock
        if (book.getAvailableStock() < requestedTotal) {
            throw new InsufficientStockException(
                    "Insufficient stock for book '" + book.getTitle() + "'. Available: " + book.getAvailableStock()
                            + ", in your cart: " + currentInCart + ", requested additional: " + request.getQuantity());
        }

        List<CartItem> items = cart.getItems();
        if (items == null) {
            items = new ArrayList<>();
            cart.setItems(items);
        }

        CartItem existingItem = items.stream()
                .filter(i -> i.getBookId().equals(request.getBookId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            existingItem.setSubTotal(existingItem.getQuantity() * existingItem.getPriceAtAdding());
        } else {
            CartItem newItem = new CartItem();
            newItem.setBookId(book.getBookId());
            newItem.setTitle(book.getTitle());
            newItem.setAuthor(book.getAuthor());
            newItem.setPriceAtAdding(book.getPrice());

            newItem.setQuantity(request.getQuantity());
            newItem.setSubTotal(book.getPrice() * request.getQuantity());

            items.add(newItem);
        }

        recalculateCart(cart);
        CartDocument saved = cartRepository.save(cart);
        return CartMapper.toResponse(saved);
    }

    // 3) Update cart item quantity
    @Override
    public CartResponse updateCartItem(String userId, UpdateCartItemRequest request) {
        validateUser(userId);

        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartDocument cart = getOrCreateActiveCart(userId);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getBookId().equals(request.getBookId()))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(
                        "Update failed. Item with ID '" + request.getBookId() + "' not found in your cart."));

        BookDocument book = bookRepository.findByBookId(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException(
                        "Update failed. Book not found with ID: " + request.getBookId()));

        if (book.getAvailableStock() < request.getQuantity()) {
            throw new InsufficientStockException("Cannot update quantity for '" + book.getTitle() + "'. Only "
                    + book.getAvailableStock() + " units are available in stock.");
        }

        item.setQuantity(request.getQuantity());
        item.setSubTotal(item.getPriceAtAdding() * request.getQuantity());

        recalculateCart(cart);
        CartDocument saved = cartRepository.save(cart);
        return CartMapper.toResponse(saved);
    }

    // 4) Remove item from cart
    @Override
    public void removeCartItem(String userId, String bookId) {
        validateUser(userId);

        CartDocument cart = getOrCreateActiveCart(userId);

        if (cart.getItems() != null) {
            boolean wasRemoved = cart.getItems().removeIf(i -> i.getBookId().equals(bookId));
            if (!wasRemoved) {
                throw new CartItemNotFoundException(
                        "Remove failed. Item with ID '" + bookId + "' not found in your cart.");
            }
        }

        recalculateCart(cart);
        CartDocument saved = cartRepository.save(cart);
        CartMapper.toResponse(saved);
    }

    // 5) Clear cart
    @Override
    public void clearCart(String userId) {
        validateUser(userId);
        CartDocument cart = getOrCreateActiveCart(userId);
        cart.setItems(new ArrayList<>());
        recalculateCart(cart);
        cartRepository.save(cart);
    }

    // Helper methods

    private CartDocument getOrCreateActiveCart(String userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> {
                    CartDocument cart = new CartDocument();
                    cart.setUserId(userId);
                    cart.setStatus(CartStatus.ACTIVE);
                    cart.setItems(new ArrayList<>());
                    cart.setTotalAmount(0.0);
                    cart.setTotalItems(0);
                    return cartRepository.save(cart);
                });
    }

    private void recalculateCart(CartDocument cart) {
        double totalAmount = 0;
        int totalItems = 0;

        if (cart.getItems() != null) {
            for (CartItem item : cart.getItems()) {
                totalAmount += item.getSubTotal();
                totalItems += item.getQuantity();
            }
        }

        cart.setTotalAmount(totalAmount);
        cart.setTotalItems(totalItems);
    }
}
