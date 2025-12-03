package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.dto.UserAdminDTO;
import com.bookstoreapp.springboot.book_store_app.dto.UserMeDTO;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserMeDTO userMeDTO){
        userService.register(userMeDTO);
        return ResponseEntity.ok("User created successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserAdminDTO> getUsers(){

        return userService.getUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminDTO getUserById(@PathVariable("id") Long id){
        return userService.getUserById(id);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> deleteBook(@PathVariable("id") Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // "/me"

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public UserMeDTO getUserDetails() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserMeDTO dto = userService.getUserDetails(username);

        if (dto == null) {
            throw new UserNotFoundException(username);
        }
        return dto;
    }


}