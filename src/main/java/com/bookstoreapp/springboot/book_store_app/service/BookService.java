package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.exception.BookNotFoundException;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.mapper.BookMapper;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import com.bookstoreapp.springboot.book_store_app.model.CartItem;
import com.bookstoreapp.springboot.book_store_app.model.User;
import com.bookstoreapp.springboot.book_store_app.repository.BookRepository;
import com.bookstoreapp.springboot.book_store_app.repository.CartItemRepository;
import com.bookstoreapp.springboot.book_store_app.repository.CartRepository;
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

    private CartItemRepository cartItemRepository;

    private CartRepository cartRepository;

    private UserRepository userRepository;

    private BookMapper bookMapper;

    public BookService(BookRepository booksRepository, CartRepository cartRepository,CartItemRepository cartItemRepository, UserRepository userRepository
            ,BookMapper bookMapper) {

        this.bookRepository = booksRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.bookMapper = bookMapper;
    }

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

    public BookDTO createBook(BookDTO bookDTO)
    {
        Book book = bookRepository.save(bookMapper.toEntity(bookDTO));
        return bookMapper.toDTO(book);
    }

    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {

    Book existingBook = bookRepository.findById(id)
            .orElseThrow(() -> new BookNotFoundException(id));

    bookMapper.updateBookFromDTO(bookDTO, existingBook);

    return bookMapper.toDTO(existingBook);
}

    @Transactional
    public void deleteBook(Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        cartItemRepository.deleteAllByBookId(id);

        // Αφαίρεση από τα favorites όλων των χρηστών
        userRepository.removeBookFromAllFavorites(id);

        // Διαγραφή του βιβλίου από τον κατάλογο
        bookRepository.delete(book);
    }
}
