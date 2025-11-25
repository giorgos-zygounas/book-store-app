package com.bookstoreapp.springboot.book_store_app.repository;

import com.bookstoreapp.springboot.book_store_app.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findAll();
}
