package com.bookstoreapp.springboot.book_store_app.repository;

import com.bookstoreapp.springboot.book_store_app.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findCartItemByCartIdAndBookId(Long cartId, Long bookId);
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.book.id = :bookId")
    void deleteAllByBookId(@Param("bookId") Long bookId);

}
