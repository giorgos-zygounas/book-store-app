package com.bookstoreapp.springboot.book_store_app.mapper;

import com.bookstoreapp.springboot.book_store_app.dto.AdminOrderDTO;
import com.bookstoreapp.springboot.book_store_app.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface AdminOrderMapper {

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    AdminOrderDTO toDTO(Order order);
}
