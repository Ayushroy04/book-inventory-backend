package com.example.book_inventory.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResponse {

    private String reviewId;
    private String bookId;
    private String userId;
    private String username;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
