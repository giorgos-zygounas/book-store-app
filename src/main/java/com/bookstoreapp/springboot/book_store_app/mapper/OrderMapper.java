package com.bookstoreapp.springboot.book_store_app.mapper;

import com.bookstoreapp.springboot.book_store_app.dto.OrderDTO;
import com.bookstoreapp.springboot.book_store_app.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    OrderDTO toDTO(Order order);
}
