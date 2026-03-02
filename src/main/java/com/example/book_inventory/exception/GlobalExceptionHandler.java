package com.example.book_inventory.exception;

import com.example.book_inventory.exception.Cart.CartItemNotFoundException;
import com.example.book_inventory.exception.domain.InsufficientStockException;
import com.example.book_inventory.exception.User.DuplicateUserException;
import com.example.book_inventory.exception.User.EmailAlreadyExistsException;
import com.example.book_inventory.exception.User.UserNotFoundException;
import com.example.book_inventory.exception.book.BookNotFoundException;
import com.example.book_inventory.exception.book.DuplicateBookException;
import com.example.book_inventory.exception.order.OrderNotFoundException;
import com.example.book_inventory.exception.review.DuplicateReviewException;
import com.example.book_inventory.exception.review.ReviewNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(BookNotFoundException.class)
        public ResponseEntity<ApiError> BookNotFoundException(
                        BookNotFoundException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.NOT_FOUND.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());

                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidationErrors(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.BAD_REQUEST.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());

                Map<String, String> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                                .collect(
                                                java.util.stream.Collectors.toMap(
                                                                fieldError -> fieldError.getField(),
                                                                fieldError -> fieldError.getDefaultMessage()));
                apiError.setValidationErrors(validationErrors);
                return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(DuplicateBookException.class)
        public ResponseEntity<ApiError> handleDuplicateBookException(
                        DuplicateBookException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();

                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.CONFLICT.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());
                return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(DuplicateUserException.class)
        public ResponseEntity<ApiError> handleDuplicateUserException(
                        DuplicateUserException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.CONFLICT.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());
                return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<ApiError> handleUserNotFoundException(
                        UserNotFoundException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.NOT_FOUND.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());
                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<ApiError> handleEmailAlreadyExists(
                        EmailAlreadyExistsException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.CONFLICT.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());
                return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(CartItemNotFoundException.class)
        public ResponseEntity<ApiError> handleCartItemNotFoundException(
                        CartItemNotFoundException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.NOT_FOUND.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());
                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler
        public ResponseEntity<ApiError> handleInsufficientStockException(
                        InsufficientStockException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.CONFLICT.value());
                ;
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());
                return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
        }

        @ExceptionHandler(OrderNotFoundException.class)
        public ResponseEntity<ApiError> handleOrderNotFoundException(
                        OrderNotFoundException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.NOT_FOUND.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());

                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
        public ResponseEntity<ApiError> handleAccessDeniedException(
                        org.springframework.security.access.AccessDeniedException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.FORBIDDEN.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());

                return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(ReviewNotFoundException.class)
        public ResponseEntity<ApiError> handleReviewNotFoundException(
                        ReviewNotFoundException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.NOT_FOUND.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());
                return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(DuplicateReviewException.class)
        public ResponseEntity<ApiError> handleDuplicateReviewException(
                        DuplicateReviewException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage());
                apiError.setStatus(HttpStatus.CONFLICT.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());
                return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
        }

        // Catch-all for any unhandled runtime exceptions (e.g. Razorpay API errors)
        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ApiError> handleRuntimeException(
                        RuntimeException ex,
                        HttpServletRequest request) {
                ApiError apiError = new ApiError();
                apiError.setMessage(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.");
                apiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                apiError.setPath(request.getRequestURI());
                apiError.setTimestamp(LocalDateTime.now());
                return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
        }

}
