package com.bookstoreapp.springboot.book_store_app.mapper;

import com.bookstoreapp.springboot.book_store_app.dto.OrderItemDTO;
import com.bookstoreapp.springboot.book_store_app.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "title")
    @Mapping(source = "priceAtPurchase", target = "price")
    OrderItemDTO toItemDTO(OrderItem orderItem);
}
