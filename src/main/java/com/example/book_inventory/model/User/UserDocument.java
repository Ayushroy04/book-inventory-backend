package com.example.book_inventory.model.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserDocument {

    @Id
    private String userId;

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

    private Role role;
}
