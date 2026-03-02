package com.example.book_inventory.service.Auth;

import com.example.book_inventory.dto.request.auth.LoginRequest;
import com.example.book_inventory.dto.request.auth.RegisterRequest;
import com.example.book_inventory.dto.response.auth.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

}
