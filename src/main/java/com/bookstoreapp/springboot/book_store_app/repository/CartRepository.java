package com.bookstoreapp.springboot.book_store_app.repository;

import com.bookstoreapp.springboot.book_store_app.model.Cart;
import com.bookstoreapp.springboot.book_store_app.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository <Cart,Long> {
}
