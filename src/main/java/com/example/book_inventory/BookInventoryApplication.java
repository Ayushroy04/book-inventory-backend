package com.example.book_inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BookInventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookInventoryApplication.class, args);
	}
}
