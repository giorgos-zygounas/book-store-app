package com.bookstoreapp.springboot.book_store_app.mapper;

import com.bookstoreapp.springboot.book_store_app.dto.UserAdminDTO;
import com.bookstoreapp.springboot.book_store_app.dto.UserMeDTO;
import com.bookstoreapp.springboot.book_store_app.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserMeDTO UserMeDTO);
    @Mapping(source = "createdAt", target = "createdAt")
    UserAdminDTO toAdminDTO(User user);
    UserMeDTO toUserMeDTO(User user);
}
