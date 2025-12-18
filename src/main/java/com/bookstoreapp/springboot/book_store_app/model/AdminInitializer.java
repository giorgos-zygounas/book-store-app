package com.bookstoreapp.springboot.book_store_app.model;

import com.bookstoreapp.springboot.book_store_app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            repo.findByUsername("admin").orElseGet(() -> {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setEmail("admin@example.com");
                admin.setFirstName("System");
                admin.setLastName("Admin");
                admin.setRole("ADMIN");
                admin.setCreatedAt(LocalDateTime.now());
                return repo.save(admin);
            });
        };
    }

}
