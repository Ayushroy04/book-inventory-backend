package com.example.book_inventory.model.Wishlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "wishList")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishlistDocument {

    @Id
    private String id;

    private String wishlistId;
    private String userId; // one wishlist per user

    private List<String> bookIds = new ArrayList<>(); // all bookIds in one list

    private LocalDateTime createdDate;
}
