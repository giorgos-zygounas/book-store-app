package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.exception.BookNotFoundException;
import com.bookstoreapp.springboot.book_store_app.mapper.BookMapper;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import com.bookstoreapp.springboot.book_store_app.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService service;

    @MockitoBean
    private BookRepository repository;

    @MockitoBean
    private BookMapper mapper;

    @Test
    @DisplayName("Test getBooks")
    void testGetBooks() {
        Book mockBook = new Book(1L, "Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), true);
        Book mockBook2 = new Book(2L, "Coding Book", "George Willson", "Great book", new BigDecimal("39.99"), true);

        doReturn(Arrays.asList(mockBook, mockBook2)).when(repository).findAll();

        doReturn(new BookDTO("Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), true))
                .when(mapper).toDTO(mockBook);

        doReturn(new BookDTO("Coding Book", "George Willson", "Great book", new BigDecimal("39.99"), true))
                .when(mapper).toDTO(mockBook2);

        //doReturn(Arrays.asList(mapper.toDTO(mockBook), mapper.toDTO(mockBook2))).when(repository).findAll();
        List<BookDTO> books = service.getBooks();

        Assertions.assertEquals(2, books.size(), "getBooks should return 2 books");
        Assertions.assertEquals("George Willson", books.get(1).getAuthor(), "getBooks should return 2 books");
    }

    @Test
    @DisplayName("Test getBookById")
    void testGetBookByIdSuccess() {
        Book mockBook = new Book(1L, "Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), true);

        doReturn(Optional.of(mockBook)).when(repository).findById(1L);

        doReturn(new BookDTO("Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), true))
                .when(mapper).toDTO(mockBook);

        //doReturn(Arrays.asList(mapper.toDTO(mockBook), mapper.toDTO(mockBook2))).when(repository).findAll();
        BookDTO book = service.getBookById(1L);

        Assertions.assertEquals(book.getTitle(), mockBook.getTitle(), "Book titles should be the same");
        Assertions.assertEquals(book.getPrice(), mockBook.getPrice(), "Price should be the same");
    }

    @Test
    @DisplayName("Test getBookById Not Found")
    void testGetBookByIdNotFound() {
        // Mock: repository δεν βρίσκει βιβλίο
        when(repository.findById(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, ()-> service.getBookById(1L),
                "Excpected BookNotFoundException when book is not found");

        assertEquals("Book with id 1 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test create Book")
    void testCreateBook() {
        Book mockBook = new Book(1L, "Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), true);
        BookDTO expectedDTO = new BookDTO("Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), true);

        doReturn(mockBook).when(repository).save(any());
        when(mapper.toDTO(mockBook)).thenReturn(expectedDTO);

        BookDTO returnedBook = service.createBook(expectedDTO);

        assertThat(returnedBook).isEqualTo(expectedDTO);
    }

    @Test
    @DisplayName("Test updateBook - Success")
    void testUpdateBookSuccess() {
        Book newBook = new Book(); // θα δημιουργηθεί από το service
        BookDTO expectedDTO = new BookDTO("New Title", "New Author", "Nice book", new BigDecimal("19.99"), true);

        doReturn(Optional.of(newBook)).when(repository).findById(1L);
        doReturn(newBook).when(repository).save(any());
        when(mapper.toDTO(newBook)).thenReturn(expectedDTO);

        BookDTO returnedBook = service.updateBook(1L, expectedDTO);
        assertThat(returnedBook).isEqualTo(expectedDTO);
    }

    @Test
    @DisplayName("Test updateBook - Success")
    void testUpdateBookNotFound() {
        when(repository.existsById(1L)).thenReturn(false);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class,
                () -> service.deleteBook(1L),
                "Expected BookNotFoundException when book is not found");

        assertEquals("Book with id 1 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test delete Book by id - Success")
    void testDeleteBook() {

        Long id = 1L;
        when(repository.existsById(id)).thenReturn(true);

        service.deleteBook(id);

    }

    @Test
    @DisplayName("Test delete book by id - Failure ")
    void testDeleteBookNotFound() {
        when(repository.existsById(1L)).thenReturn(false);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class,
                () -> service.deleteBook(1L),
                "Expected BookNotFoundException when book is not found");

        assertEquals("Book with id 1 not found", exception.getMessage());
    }


}



