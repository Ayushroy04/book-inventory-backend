package com.example.book_inventory.model.Review;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reviews")
@Data
@NoArgsConstructor
public class ReviewDocument {

    @Id
    private String id;

    private String reviewId;
    private String bookId;
    private String userId;
    private String username;

    private int rating; // 1 to 5
    private String comment; // optional

    private LocalDateTime createdAt;
}
