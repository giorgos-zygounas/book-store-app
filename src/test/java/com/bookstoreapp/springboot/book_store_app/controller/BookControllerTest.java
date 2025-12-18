package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.config.SecurityConfig;
import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.exception.BookNotFoundException;
import com.bookstoreapp.springboot.book_store_app.exception.GlobalExceptionHandler;
import com.bookstoreapp.springboot.book_store_app.model.AvailabilityStatus;
import com.bookstoreapp.springboot.book_store_app.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;


import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = BookController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        })
public class BookControllerTest {

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test get books")
    @WithMockUser(roles = "ADMIN")
    void testGetBooks() throws Exception {

        List<BookDTO> mockBooks = List.of(
                new BookDTO(1L,"Title 1", "Author 1", "Good book", new BigDecimal("10.99"), AvailabilityStatus.AVAILABLE),
                new BookDTO(1L,"Title 2", "Author 2", "Nice book", new BigDecimal("19.99"), AvailabilityStatus.AVAILABLE)
        );

        when(bookService.getBooks()).thenReturn(mockBooks);

        mockMvc.perform(get("/books"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))


                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].title", is("Title 1")))
                .andExpect(jsonPath("$[0].author", is("Author 1")))
                .andExpect(jsonPath("$[0].description", is("Good book")))
                .andExpect(jsonPath("$[0].price", is(10.99)))
                .andExpect(jsonPath("$[0].available", is("AVAILABLE")))
                .andExpect(jsonPath("$[1].title", is("Title 2")))
                .andExpect(jsonPath("$[1].author", is("Author 2")))
                .andExpect(jsonPath("$[1].description", is("Nice book")))
                .andExpect(jsonPath("$[1].price", is(19.99)))
                .andExpect(jsonPath("$[1].available", is("AVAILABLE")));

    }

    @Test
    @DisplayName("Test get book by id - Success")
    @WithMockUser
    void testGetBookByIdSuccess() throws Exception {
        BookDTO expectedDTO = new BookDTO(1L,"Title 1", "Author 1", "Good book", new BigDecimal("10.99"), AvailabilityStatus.AVAILABLE);

        when(bookService.getBookById(1L)).thenReturn(expectedDTO);

        mockMvc.perform(get("/books/{id}", 1))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.title", is("Title 1")))
                .andExpect(jsonPath("$.author", is("Author 1")))
                .andExpect(jsonPath("$.description", is("Good book")))
                .andExpect(jsonPath("$.price", is(10.99)))
                .andExpect(jsonPath("$.available", is("AVAILABLE")));
    }

    @Test
    @DisplayName("Test get book by id not found")
    @WithMockUser
    void testGetBookByIdNotFound() throws Exception {

        doThrow(new BookNotFoundException(1L)).when(bookService).getBookById(1L);

        mockMvc.perform(get("/books/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Book with id 1 not found")));
    }

    //TODO POST BOOKS, PUT AND DELETE(SUCCESS AND NOT FOUND), MAPPER TESTS, EXCEPTIONS AND VALIDATIONS(LATER)
    @Test
    @DisplayName("Test create book")
    @WithMockUser(roles = "ADMIN")
    void testCreateBook() throws Exception {
        BookDTO postBookDTO = new BookDTO(1L,"Title 1", "Author 1", "Good book", new BigDecimal("10.99"), AvailabilityStatus.AVAILABLE);
        BookDTO mockBookDTO = new BookDTO(1L,"Title 1", "Author 1", "Good book", new BigDecimal("10.99"), AvailabilityStatus.AVAILABLE);
        doReturn(mockBookDTO).when(bookService).createBook(postBookDTO);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(postBookDTO)))
                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.title", is("Title 1")))
                .andExpect(jsonPath("$.author", is("Author 1")))
                .andExpect(jsonPath("$.description", is("Good book")))
                .andExpect(jsonPath("$.price", is(10.99)))
                .andExpect(jsonPath("$.available", is("AVAILABLE")));

    }

    @Test
    @DisplayName("Test update book details - Success")
    @WithMockUser(roles = "ADMIN")
    void updateBookSuccess() throws Exception {
        BookDTO putBookDTO = new BookDTO(1L,"Title 1", "Author 1", "Good book", new BigDecimal("10.99"), AvailabilityStatus.AVAILABLE);
        BookDTO mockBookDTO = new BookDTO(1L,"Title 1", "Author 1", "Good book", new BigDecimal("10.99"), AvailabilityStatus.AVAILABLE);

        doReturn(mockBookDTO).when(bookService).updateBook(1L, putBookDTO);
        mockMvc.perform(put("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(putBookDTO)))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.title", is("Title 1")))
                .andExpect(jsonPath("$.author", is("Author 1")))
                .andExpect(jsonPath("$.description", is("Good book")))
                .andExpect(jsonPath("$.price", is(10.99)))
                .andExpect(jsonPath("$.available", is("AVAILABLE")));
    }

    @Test
    @DisplayName("Test update book details failed")
    @WithMockUser(roles = "ADMIN")
    void testUpdateBookFailed() throws Exception {
        BookDTO putBookDTO = new BookDTO(1L,"Title 1", "Author 1", "Good book", new BigDecimal("10.99"), AvailabilityStatus.AVAILABLE);

        doThrow(new BookNotFoundException(1L)).when(bookService).updateBook(1L, putBookDTO);

        mockMvc.perform(put("/books/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(putBookDTO)))

                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Book with id 1 not found")));
    }

    @Test
    @DisplayName("Test delete book - Success")
    @WithMockUser(roles = "ADMIN")
    void testDeleteBookSuccess() throws Exception {

        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/{id}", 1))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("Test delete book failed")
    @WithMockUser(roles = "ADMIN")
    void testDeleteBookFailed() throws Exception {

        doThrow(new BookNotFoundException(1L)).when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Book with id 1 not found")));

    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
