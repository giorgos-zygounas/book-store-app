package com.bookstoreapp.springboot.book_store_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDTO {
    private Long bookId;
    private String title;
    private BigDecimal price;
    private int quantity;
}
