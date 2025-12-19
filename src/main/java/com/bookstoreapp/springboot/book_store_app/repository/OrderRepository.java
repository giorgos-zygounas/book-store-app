package com.bookstoreapp.springboot.book_store_app.repository;

import com.bookstoreapp.springboot.book_store_app.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
