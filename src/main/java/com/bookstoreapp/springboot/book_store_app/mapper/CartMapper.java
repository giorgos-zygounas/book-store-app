package com.bookstoreapp.springboot.book_store_app.mapper;

import com.bookstoreapp.springboot.book_store_app.dto.CartDTO;
import com.bookstoreapp.springboot.book_store_app.dto.CartItemDTO;
import com.bookstoreapp.springboot.book_store_app.model.Cart;
import com.bookstoreapp.springboot.book_store_app.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {
    @Mapping(source = "createdAt", target = "createdAt")
    CartDTO toDTO(Cart cart);

}
