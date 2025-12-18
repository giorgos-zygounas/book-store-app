package com.bookstoreapp.springboot.book_store_app.dto;

import com.bookstoreapp.springboot.book_store_app.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {

    private Long id;
    private List<CartItemDTO> cartItems;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
