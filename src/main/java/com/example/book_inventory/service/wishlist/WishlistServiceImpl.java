package com.example.book_inventory.service.wishlist;

import com.example.book_inventory.config.UserPrincipal;
import com.example.book_inventory.dto.response.wishlist.WishlistBookItem;
import com.example.book_inventory.dto.response.wishlist.WishlistResponse;
import com.example.book_inventory.exception.book.BookNotFoundException;
import com.example.book_inventory.model.Book.BookDocument;
import com.example.book_inventory.model.Wishlist.WishlistDocument;
import com.example.book_inventory.repository.BookRepository;
import com.example.book_inventory.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;

    @Override
    public WishlistResponse getWishlist() {
        String userId = getCurrentUserId();
        WishlistDocument wishlist = getOrCreateWishlist(userId);
        return toResponse(wishlist);
    }

    @Override
    public WishlistResponse addBook(String bookId) {
        // Verify the book exists and is active
        bookRepository.findByBookId(bookId)
                .filter(book -> Boolean.TRUE.equals(book.getActive()))
                .orElseThrow(() -> new BookNotFoundException(
                        "Cannot add to wishlist. Book not found with ID: " + bookId));

        String userId = getCurrentUserId();
        WishlistDocument wishlist = getOrCreateWishlist(userId);

        // Only add if not already in wishlist (no duplicates)
        if (!wishlist.getBookIds().contains(bookId)) {
            wishlist.getBookIds().add(bookId);
            wishlistRepository.save(wishlist);
        }

        return toResponse(wishlist);
    }

    @Override
    public WishlistResponse removeBook(String bookId) {
        String userId = getCurrentUserId();
        WishlistDocument wishlist = getOrCreateWishlist(userId);

        wishlist.getBookIds().remove(bookId);
        wishlistRepository.save(wishlist);

        return toResponse(wishlist);
    }

    // ── Helper Methods ──────────────────────────────────────────────────────────

    /**
     * Returns the existing wishlist for the user, or creates a new empty one.
     */
    private WishlistDocument getOrCreateWishlist(String userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    WishlistDocument newWishlist = new WishlistDocument();
                    newWishlist.setWishlistId(UUID.randomUUID().toString());
                    newWishlist.setUserId(userId);
                    newWishlist.setCreatedDate(LocalDateTime.now());
                    return wishlistRepository.save(newWishlist);
                });
    }

    /**
     * Maps WishlistDocument → WishlistResponse, enriching bookIds with full book
     * details.
     */
    private WishlistResponse toResponse(WishlistDocument wishlist) {
        List<WishlistBookItem> bookItems = wishlist.getBookIds().stream()
                .map(bookId -> bookRepository.findByBookId(bookId)
                        .map(this::toBookItem)
                        .orElse(null))
                .filter(item -> item != null) // skip deleted books
                .collect(Collectors.toList());

        return WishlistResponse.builder()
                .wishlistId(wishlist.getWishlistId())
                .userId(wishlist.getUserId())
                .books(bookItems)
                .totalItems(bookItems.size())
                .build();
    }

    private WishlistBookItem toBookItem(BookDocument book) {
        return WishlistBookItem.builder()
                .bookId(book.getBookId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .genre(book.getGenre())
                .outOfStock(book.getAvailableStock() == null || book.getAvailableStock() == 0)
                .build();
    }

    private String getCurrentUserId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getUserId();
    }
}
