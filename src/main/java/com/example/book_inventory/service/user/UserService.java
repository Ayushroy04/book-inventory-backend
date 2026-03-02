package com.example.book_inventory.service.user;

import com.example.book_inventory.dto.request.user.CreateUserRequest;
import com.example.book_inventory.dto.request.user.UpdateUserRequest;
import com.example.book_inventory.dto.response.PageResponse;
import com.example.book_inventory.dto.response.user.UpdateUserResponse;
import com.example.book_inventory.dto.response.user.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    PageResponse<UserResponse> getAllUsers(int page, int size);

    UpdateUserResponse updateUser(String userId, UpdateUserRequest request);

    void deleteUser(String userId);

    UserResponse getUserByUserId(String userId);

}
