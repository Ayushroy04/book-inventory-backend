package com.example.book_inventory.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserResponse {
    private String userId;
    private String username;
    private String email;
}
