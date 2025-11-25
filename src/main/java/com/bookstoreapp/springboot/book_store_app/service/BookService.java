package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.mapper.BookMapper;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import com.bookstoreapp.springboot.book_store_app.repository.BookRepository;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class BookService {

    private BookRepository bookRepository;

    private BookMapper bookMapper;

    public BookService(BookRepository booksRepository, BookMapper bookMapper) {

        this.bookRepository = booksRepository;
        this.bookMapper = bookMapper;
    }
//ετσι η με stream
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
                .orElse(new BookDTO());
    }
    //create update
    public BookDTO createBook(BookDTO bookDTO)
    {

        Book book = bookRepository.save(bookMapper.toEntity(bookDTO));
        return bookMapper.toDTO(book);
    }
    //na to ftiaksw, na kanw git
    public BookDTO updateBook(Long id, BookDTO bookDTO){
        Book existingBook =  bookRepository.findById(id)
                .orElse(new Book());

        bookMapper.updateBookFromDTO(bookDTO, existingBook);

        return bookMapper.toDTO(bookRepository.save(existingBook));
    }

}
