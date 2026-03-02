package com.example.book_inventory.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookRatingResponse {

    private String bookId;
    private double averageRating; // e.g. 4.3
    private int totalReviews; // total number of reviews
}
