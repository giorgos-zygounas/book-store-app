package com.bookstoreapp.springboot.book_store_app.mapper;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class BookMapperTest {


    private BookMapper bookMapper;

    @BeforeEach
    public void setup() {

        bookMapper = Mappers.getMapper(BookMapper.class);
    }

    @Test
    public void whenMappingDtoToEntity_thenFieldsMatch() {
        BookDTO dto = new BookDTO("Title 1", "Author 1", "Good book", new BigDecimal("10.99"), true);

        Book result = bookMapper.toEntity(dto);

        assertNotNull(result);
        assertEquals("Title 1", result.getTitle());
        assertEquals("Author 1", result.getAuthor());
        assertEquals("Good book", result.getDescription());
        assertEquals(new BigDecimal("10.99"), result.getPrice());
        assertTrue(result.isAvailable());
    }

    @Test
    public void whenMappingEntityToDto_thenFieldsMatch() {
        Book bookEntity = new Book(1L, "Title 1", "Author 1", "Good book", new BigDecimal("10.99"), true);

        BookDTO result = bookMapper.toDTO(bookEntity);

        assertNotNull(result);
        assertEquals("Title 1", result.getTitle());
        assertEquals("Author 1", result.getAuthor());
        assertEquals("Good book", result.getDescription());
        assertEquals(new BigDecimal("10.99"), result.getPrice());
        assertTrue(result.isAvailable());
    }

    @Test
    public void whenMappingDtoToEntityAndBack_thenDataIsConsistent() {
        BookDTO dto = new BookDTO("Title 1", "Author 1", "Good book", new BigDecimal("10.99"), true);

        Book entity = bookMapper.toEntity(dto);
        BookDTO result = bookMapper.toDTO(entity);

        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(dto.getAuthor(), result.getAuthor());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getPrice(), result.getPrice());
        assertEquals(dto.isAvailable(), result.isAvailable());
    }

    @Test
    public void testUpdateBookFromDto() {
        Book bookEntity = new Book(1L, "Old Title", "Old Author", "Old Desc", new BigDecimal("5.00"), false);
        BookDTO dto = new BookDTO("New Title", "New Author", "New Desc", new BigDecimal("9.99"), true);

        bookMapper.updateBookFromDTO(dto, bookEntity);

        assertEquals(1L, bookEntity.getId()); // ID should remain unchanged
        assertEquals("New Title", bookEntity.getTitle());
        assertEquals("New Author", bookEntity.getAuthor());
        assertEquals("New Desc", bookEntity.getDescription());
        assertEquals(new BigDecimal("9.99"), bookEntity.getPrice());
        assertTrue(bookEntity.isAvailable());
    }
}



