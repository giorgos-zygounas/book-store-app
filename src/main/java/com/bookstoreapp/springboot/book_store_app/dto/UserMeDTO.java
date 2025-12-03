package com.bookstoreapp.springboot.book_store_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMeDTO {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
}
