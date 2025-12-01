package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.exception.BookNotFoundException;
import com.bookstoreapp.springboot.book_store_app.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;


import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @MockitoBean
    private BookService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Test get books")
    void testGetBooks() throws Exception{

        List<BookDTO> mockBooks = List.of(
                new BookDTO("Title 1", "Author 1", "Good book", new BigDecimal("10.99"), true),
                new BookDTO("Title 2", "Author 2", "Nice book", new BigDecimal("19.99"), true)
        );

        when(service.getBooks()).thenReturn(mockBooks);

        mockMvc.perform(get("/books"))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))


                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].title", is("Title 1")))
                .andExpect(jsonPath("$[0].author", is("Author 1")))
                .andExpect(jsonPath("$[0].description", is("Good book")))
                .andExpect(jsonPath("$[0].price", is(10.99)))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[1].title", is("Title 2")))
                .andExpect(jsonPath("$[1].author", is("Author 2")))
                .andExpect(jsonPath("$[1].description", is("Nice book")))
                .andExpect(jsonPath("$[1].price", is(19.99)))
                .andExpect(jsonPath("$[1].available", is(true)));

    }

    @Test
    @DisplayName("Test get book by id - Success")
    void testGetBookByIdSuccess() throws Exception{
        BookDTO expectedDTO = new BookDTO("Title 1", "Author 1", "Good book"
                , new BigDecimal("10.99"), true);

        when(service.getBookById(1L)).thenReturn(expectedDTO);

        mockMvc.perform(get("/books/{id}",1))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.title", is("Title 1")))
                .andExpect(jsonPath("$.author", is("Author 1")))
                .andExpect(jsonPath("$.description", is("Good book")))
                .andExpect(jsonPath("$.price", is(10.99)))
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    @DisplayName("Test get book by id not found")
    void testGetBookByIdNotFound() throws Exception{

        doThrow(new BookNotFoundException(1L)).when(service).getBookById(1L);

        mockMvc.perform(get("/books/{id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.error", is("Book with id 1 not found")));
    }

//TODO POST BOOKS, PUT AND DELETE(SUCCESS AND NOT FOUND), MAPPER TESTS, EXCEPTIONS AND VALIDATIONS(LATER)
    @Test
    @DisplayName("Test create book")
    void testCreateBook() throws Exception{
        BookDTO postBookDTO = new BookDTO("Title 1", "Author 1", "Good book"
                , new BigDecimal("10.99"), true);
        BookDTO mockBookDTO = new BookDTO("Title 1", "Author 1", "Good book"
                , new BigDecimal("10.99"), true);
        doReturn(mockBookDTO).when(service).createBook(postBookDTO);

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
                .andExpect(jsonPath("$.available", is(true)));

    }
    @Test
    @DisplayName("Test update book details - Success")
    void updateBookSuccess() throws Exception{
        BookDTO putBookDTO = new BookDTO("Title 1", "Author 1", "Good book"
                , new BigDecimal("10.99"), true);
        BookDTO mockBookDTO = new BookDTO("Title 1", "Author 1", "Good book"
                , new BigDecimal("10.99"), true);

        doReturn(mockBookDTO).when(service).updateBook(1L, putBookDTO);
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
                .andExpect(jsonPath("$.available", is(true)));
    }

    @Test
    @DisplayName("Test update book details failed")
    void testUpdateBookFailed() throws Exception{
        BookDTO putBookDTO = new BookDTO("Title 1", "Author 1", "Good book"
                , new BigDecimal("10.99"), true);

        doThrow(new BookNotFoundException(1L)).when(service).updateBook(1L, putBookDTO);

        mockMvc.perform(put("/books/{id}", 1)

                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(putBookDTO)))

                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is("Book with id 1 not found")));
    }

    @Test
    @DisplayName("Test delete book - Success")
    void testDeleteBookSuccess() throws Exception{

        doNothing().when(service).deleteBook(1L);

        mockMvc.perform(delete("/books/{id}", 1))
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Test delete book failed")
    void testDeleteBookFailed() throws Exception{

        doThrow(new BookNotFoundException(1L)).when(service).deleteBook(1L);

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
