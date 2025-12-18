package com.bookstoreapp.springboot.book_store_app.mapper;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toEntity(BookDTO bookDTO);
    BookDTO toDTO(Book book);
    @Mapping(target = "id", ignore = true)
    void  updateBookFromDTO(BookDTO bookDTO, @MappingTarget Book book);
}



