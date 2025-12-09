package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.exception.BookNotFoundException;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.mapper.BookMapper;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import com.bookstoreapp.springboot.book_store_app.model.User;
import com.bookstoreapp.springboot.book_store_app.repository.BookRepository;
import com.bookstoreapp.springboot.book_store_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.mapstruct.MappingTarget;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class BookService {

    private BookRepository bookRepository;
    private UserRepository userRepository;

    private BookMapper bookMapper;

    public BookService(BookRepository booksRepository, UserRepository userRepository
            ,BookMapper bookMapper) {

        this.bookRepository = booksRepository;
        this.userRepository = userRepository;
        this.bookMapper = bookMapper;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<BookDTO> getBooks(){

        var result = bookRepository.findAll()
                .stream()
                .map(bookMapper::toDTO)
                .toList();
        return result;
    }

    public BookDTO getBookById(Long id){
        return bookRepository.findById(id)
                .map(bookMapper::toDTO)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BookDTO createBook(BookDTO bookDTO)
    {
        Book book = bookRepository.save(bookMapper.toEntity(bookDTO));
        return bookMapper.toDTO(book);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public BookDTO updateBook(Long id, BookDTO bookDTO){

        Book existingBook =  bookRepository.findById(id)
                        .orElseThrow(() -> new BookNotFoundException(id));
        bookMapper.updateBookFromDTO(bookDTO, existingBook);
        return bookMapper.toDTO(bookRepository.save(existingBook));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        // Αφαίρεση από τα favorites όλων των χρηστών
        userRepository.removeBookFromAllFavorites(id);

        // Διαγραφή του βιβλίου από τον κατάλογο
        bookRepository.delete(book);
    }
}
