package com.bookstoreapp.springboot.book_store_app.dto;

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
public class AdminOrderDTO {

    private Long id;
    private String username;
    private String email;
    private List<OrderItemDTO> orderItems;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
