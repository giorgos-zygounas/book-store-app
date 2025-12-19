package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;

import com.bookstoreapp.springboot.book_store_app.dto.UserAdminDTO;
import com.bookstoreapp.springboot.book_store_app.dto.UserMeDTO;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
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

    @GetMapping("/login")
    public ResponseEntity<String> login(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Welcome " + username + "!");
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

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> updateUser(@RequestBody UserMeDTO updateDTO){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserMeDTO updatedUser = userService.updateUser(username, updateDTO);
        return ResponseEntity.ok("User details updated successfully");
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> deleteUser(@PathVariable("id") Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }

    // "/me"
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserMeDTO getUserDetails() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserMeDTO dto = userService.getUserDetails(username);
        return dto;
    }

    @GetMapping("/me/favorites")
    @PreAuthorize("hasRole('USER')")
    public List<BookDTO> getFavoriteBooks(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getFavoriteBooks(username);
    }

    @PostMapping("/me/favorites/{book_id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> addBookToFavorites(@PathVariable("book_id") Long bookId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.addFavoriteBook(username, bookId);
        return ResponseEntity.ok("Book added to favorites");
    }

    @DeleteMapping("/me/favorites/{book_id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> removeBookFromFavorites(@PathVariable("book_id") Long bookId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.removeFavoriteBook(username, bookId);
        return ResponseEntity.ok("Book removed from favorites");

    }

}