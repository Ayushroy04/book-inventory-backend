package com.example.book_inventory.service.Auth;

import com.example.book_inventory.model.User.Role;
import com.example.book_inventory.service.security.JwtService;

import com.example.book_inventory.dto.request.auth.LoginRequest;
import com.example.book_inventory.dto.request.auth.RegisterRequest;
import com.example.book_inventory.dto.response.auth.AuthResponse;
import com.example.book_inventory.exception.User.DuplicateUserException;
import com.example.book_inventory.exception.User.EmailAlreadyExistsException;
import com.example.book_inventory.model.User.UserDocument;
import com.example.book_inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new DuplicateUserException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        UserDocument user = new UserDocument();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ encode password
        user.setRole(Role.USER);

        UserDocument savedUser = userRepository.save(user);

        // generate JWT token after register
        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(
                token,
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                "User registered successfully");
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // ✅ Let Spring Security validate email/password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // ✅ if above line succeeds => user is valid
        UserDocument user = userRepository.findByEmail(request.getEmail());

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getUserId(),
                user.getEmail(),
                user.getRole().name(),
                "Login successful");
    }
}
