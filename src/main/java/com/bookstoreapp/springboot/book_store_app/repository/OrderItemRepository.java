package com.bookstoreapp.springboot.book_store_app.repository;

import com.bookstoreapp.springboot.book_store_app.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
