package com.bookstoreapp.springboot.book_store_app.mapper;

import com.bookstoreapp.springboot.book_store_app.dto.CartItemDTO;
import com.bookstoreapp.springboot.book_store_app.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "title")
    @Mapping(source = "book.price", target = "price")
    CartItemDTO toItemDTO(CartItem item);
}
