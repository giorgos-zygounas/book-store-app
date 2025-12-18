package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.exception.BookNotFoundException;
import com.bookstoreapp.springboot.book_store_app.mapper.BookMapper;
import com.bookstoreapp.springboot.book_store_app.model.AvailabilityStatus;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import com.bookstoreapp.springboot.book_store_app.repository.BookRepository;
import com.bookstoreapp.springboot.book_store_app.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
    private BookRepository bookRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BookMapper mapper;

    @Test
    @DisplayName("Test getBooks")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetBooks() {
        Book mockBook = new Book(1L, "Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), AvailabilityStatus.AVAILABLE);
        Book mockBook2 = new Book(2L, "Coding Book", "George Willson", "Great book", new BigDecimal("39.99"), AvailabilityStatus.AVAILABLE);

        doReturn(Arrays.asList(mockBook, mockBook2)).when(bookRepository).findAll();

        doReturn(new BookDTO(1L,"Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), AvailabilityStatus.AVAILABLE))
                .when(mapper).toDTO(mockBook);

        doReturn(new BookDTO(1L,"Coding Book", "George Willson", "Great book", new BigDecimal("39.99"), AvailabilityStatus.AVAILABLE))
                .when(mapper).toDTO(mockBook2);

        //doReturn(Arrays.asList(mapper.toDTO(mockBook), mapper.toDTO(mockBook2))).when(repository).findAll();
        List<BookDTO> books = service.getBooks();

        Assertions.assertEquals(2, books.size(), "getBooks should return 2 books");
        Assertions.assertEquals("George Willson", books.get(1).getAuthor(), "getBooks should return 2 books");
    }

    @Test
    @DisplayName("Test getBookById")
    void testGetBookByIdSuccess() {
        Book mockBook = new Book(1L, "Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), AvailabilityStatus.AVAILABLE);

        doReturn(Optional.of(mockBook)).when(bookRepository).findById(1L);

        doReturn(new BookDTO(1L,"Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), AvailabilityStatus.AVAILABLE))
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
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> service.getBookById(1L),
                "Excpected BookNotFoundException when book is not found");

        assertEquals("Book with id 1 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test create Book")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateBook() {
        Book mockBook = new Book(1L, "Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), AvailabilityStatus.AVAILABLE);
        BookDTO expectedDTO = new BookDTO(1L,"Test Book", "Jones Smith", "Nice book", new BigDecimal("19.99"), AvailabilityStatus.AVAILABLE);

        doReturn(mockBook).when(bookRepository).save(any());
        when(mapper.toDTO(mockBook)).thenReturn(expectedDTO);

        BookDTO returnedBook = service.createBook(expectedDTO);

        assertThat(returnedBook).isEqualTo(expectedDTO);
    }

    @Test
    @DisplayName("Test updateBook - Success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateBookSuccess() {
        Book newBook = new Book(); // θα δημιουργηθεί από το service
        BookDTO expectedDTO = new BookDTO(1L,"New Title", "New Author", "Nice book", new BigDecimal("19.99"), AvailabilityStatus.AVAILABLE);

        doReturn(Optional.of(newBook)).when(bookRepository).findById(1L);
        doReturn(newBook).when(bookRepository).save(any());
        when(mapper.toDTO(newBook)).thenReturn(expectedDTO);

        BookDTO returnedBook = service.updateBook(1L, expectedDTO);
        assertThat(returnedBook).isEqualTo(expectedDTO);
    }

    @Test
    @DisplayName("Test updateBook - Not Found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateBookNotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        BookNotFoundException exception = assertThrows(BookNotFoundException.class,
                () -> service.deleteBook(1L),
                "Expected BookNotFoundException when book is not found");

        assertEquals("Book with id 1 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Test delete Book by id - Success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteBook() {

        Long id = 1L;
        Book book = new Book();
        book.setId(id);

        // Mock repository για να επιστρέψει το βιβλίο
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // Mock για να αφαιρείται από όλα τα favorites
        doNothing().when(userRepository).removeBookFromAllFavorites(id);

        // Mock για delete
        doNothing().when(bookRepository).delete(book);

        // Κλήση της υπηρεσίας
        service.deleteBook(id);
    }

    @Test
    @DisplayName("Test delete Book - BookNotFoundException")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteBookFailed() {
        Long id = 1L;

        // Mock repository να επιστρέψει empty → βιβλίο δεν υπάρχει
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // Έλεγχος ότι πετάει BookNotFoundException
        assertThrows(BookNotFoundException.class, () -> service.deleteBook(id));

    }
}



