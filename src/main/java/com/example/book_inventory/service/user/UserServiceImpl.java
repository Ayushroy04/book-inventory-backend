package com.example.book_inventory.service.user;

import com.example.book_inventory.dto.request.user.CreateUserRequest;
import com.example.book_inventory.dto.request.user.UpdateUserRequest;
import com.example.book_inventory.dto.response.PageResponse;
import com.example.book_inventory.dto.response.user.UpdateUserResponse;
import com.example.book_inventory.dto.response.user.UserResponse;
import com.example.book_inventory.exception.User.DuplicateUserException;
import com.example.book_inventory.exception.User.EmailAlreadyExistsException;
import com.example.book_inventory.exception.User.UserNotFoundException;
import com.example.book_inventory.model.User.UserDocument;
import com.example.book_inventory.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(CreateUserRequest userDto) {

        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            throw new DuplicateUserException(
                    "Registration failed. Username '" + userDto.getUsername() + "' is already taken.");
        }

        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new EmailAlreadyExistsException(
                    "Registration failed. Email '" + userDto.getEmail() + "' is already registered.");
        }

        UserDocument userDocument = new UserDocument();
        userDocument.setUsername(userDto.getUsername());
        userDocument.setEmail(userDto.getEmail());
        userDocument.setPhoneNumber(userDto.getPhoneNumber());

        UserDocument savedUser = userRepository.save(userDocument);

        UserResponse response = new UserResponse();
        response.setUserId(savedUser.getUserId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());

        return response;
    }

    @Override
    public PageResponse<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDocument> userPage = userRepository.findAll(pageable);

        List<UserResponse> content = userPage.getContent().stream()
                .map(user -> {
                    UserResponse response = new UserResponse();
                    response.setUserId(user.getUserId());
                    response.setUsername(user.getUsername());
                    response.setEmail(user.getEmail());
                    return response;
                })
                .collect(Collectors.toList());

        return PageResponse.<UserResponse>builder()
                .content(content)
                .pageNumber(userPage.getNumber())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    @Override
    public UpdateUserResponse updateUser(String userId, UpdateUserRequest request) {

        UserDocument user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Update failed. User not found with ID: " + userId));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        UserDocument updatedUser = userRepository.save(user);

        UpdateUserResponse response = new UpdateUserResponse();
        response.setUserId(updatedUser.getUserId());
        response.setUsername(updatedUser.getUsername());
        response.setEmail(updatedUser.getEmail());

        return response;
    }

    @Override
    public void deleteUser(String userId) {
        UserDocument user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Delete failed. User not found with ID: " + userId));
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse getUserByUserId(String userId) {
        UserDocument user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Retrieval failed. User not found with ID: " + userId));
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        return response;
    }
}
