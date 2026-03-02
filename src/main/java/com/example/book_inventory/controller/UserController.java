package com.example.book_inventory.controller;

import com.example.book_inventory.dto.request.user.CreateUserRequest;
import com.example.book_inventory.dto.request.user.UpdateUserRequest;
import com.example.book_inventory.dto.response.PageResponse;
import com.example.book_inventory.dto.response.user.UpdateUserResponse;
import com.example.book_inventory.dto.response.user.UserResponse;
import com.example.book_inventory.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse savedUser = userService.createUser(request);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(userService.getAllUsers(page, size), HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable String userId,
            @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserResponse response = userService.updateUser(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByUserId(@PathVariable String userId) {
        return new ResponseEntity<>(userService.getUserByUserId(userId), HttpStatus.OK);
    }
}
