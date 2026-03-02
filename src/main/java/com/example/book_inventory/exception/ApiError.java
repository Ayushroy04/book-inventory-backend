package com.example.book_inventory.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {


    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;

    private Map<String,String> validationErrors;

}
