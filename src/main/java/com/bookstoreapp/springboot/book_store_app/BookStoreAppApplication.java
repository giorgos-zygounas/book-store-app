package com.bookstoreapp.springboot.book_store_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity
public class BookStoreAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookStoreAppApplication.class, args);
	}

}
