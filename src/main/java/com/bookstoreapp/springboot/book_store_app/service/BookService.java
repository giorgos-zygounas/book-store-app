package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.mapper.BookMapper;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import com.bookstoreapp.springboot.book_store_app.repository.BookRepository;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
public class BookService {

    private BookRepository bookRepository;

    private BookMapper bookMapper;

    public BookService(BookRepository booksRepository, BookMapper bookMapper) {

        this.bookRepository = booksRepository;
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
                .orElseThrow(()-> new NoSuchElementException("Book with id " + id + " not found"));
    }

    //create update
    public BookDTO createBook(BookDTO bookDTO)
    {

        Book book = bookRepository.save(bookMapper.toEntity(bookDTO));
        return bookMapper.toDTO(book);
    }

    public BookDTO updateBook(Long id, BookDTO bookDTO){
        Book existingBook =  bookRepository.findById(id)
                .orElse(new Book());

        bookMapper.updateBookFromDTO(bookDTO, existingBook);

        return bookMapper.toDTO(bookRepository.save(existingBook));
    }

}
