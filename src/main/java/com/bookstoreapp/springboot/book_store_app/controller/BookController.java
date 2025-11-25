package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import com.bookstoreapp.springboot.book_store_app.service.BookService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping(path = "/books")
public class BookController {

    private BookService bookService;

    public BookController(BookService bookService){
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDTO> getBooks(){

        return bookService.getBooks();
    }

    @PostMapping
    public BookDTO createBook(@RequestBody BookDTO bookDTO){

        return bookService.createBook(bookDTO);
    }
    @PutMapping
    public BookDTO createBook(@RequestBody BookDTO bookDTO, Long id){

        return bookService.updateBook(id, bookDTO);
    }

}

