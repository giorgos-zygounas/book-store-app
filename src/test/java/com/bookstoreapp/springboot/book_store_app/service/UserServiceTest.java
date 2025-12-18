package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.dto.UserAdminDTO;
import com.bookstoreapp.springboot.book_store_app.dto.UserMeDTO;
import com.bookstoreapp.springboot.book_store_app.exception.BookNotFoundException;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.exception.UsernameAlreadyExistsException;
import com.bookstoreapp.springboot.book_store_app.mapper.BookMapper;
import com.bookstoreapp.springboot.book_store_app.mapper.UserMapper;
import com.bookstoreapp.springboot.book_store_app.model.AuthenticatedUser;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import com.bookstoreapp.springboot.book_store_app.model.Cart;
import com.bookstoreapp.springboot.book_store_app.model.User;
import com.bookstoreapp.springboot.book_store_app.repository.BookRepository;
import com.bookstoreapp.springboot.book_store_app.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private BookMapper bookMapper;

    @MockitoBean
    PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("Test loadUserByUsername – Success")
    void testLoadUserByUsername() {
        User u = new User(1L, "John", "Smith", "john1", "pw", "USER", "john@mail.com", LocalDateTime.now(),new ArrayList<Book>(), new Cart());

        when(userRepository.findByUsername("john1")).thenReturn(Optional.of(u));

        AuthenticatedUser result = (AuthenticatedUser) userService.loadUserByUsername("john1");

        assertEquals("john1", result.getUsername());
    }


    @Test
    @DisplayName("Test loadUserByUsername – Not Found")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("unknown"));
    }

    @Test
    @DisplayName("Test register user - Success")
    void testRegisterUser(){
        UserMeDTO dto = new UserMeDTO("John", "Smith", "john123","john@mail.com");

        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setFirstName("John");
        savedUser.setLastName("Smith");
        savedUser.setUsername("john123");
        savedUser.setPassword("encoded-pw");
        savedUser.setEmail("john@mail.com");
        savedUser.setRole("USER");
        savedUser.setCreatedAt(LocalDateTime.now());

        when(passwordEncoder.encode("password")).thenReturn("encoded-pw");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.register(dto);

        assertEquals("John", result.getFirstName());
        assertEquals("encoded-pw", result.getPassword());
        assertEquals("USER", result.getRole());
    }
    @Test
    @DisplayName("Test register user - Failed")
    void testRegisterUserFailed() {

        UserMeDTO dto = new UserMeDTO("John", "Smith", "john","john@mail.com");

        when(userRepository.existsByUsername("john")).thenReturn(true);

        UsernameAlreadyExistsException ex = assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.register(dto));
        assertEquals("Username john is not available", ex.getMessage());

    }

    @Test
    @DisplayName("Test getUserDetails - Success")
    @WithMockUser(roles = "USER")
    void testGetUserDetailsSuccess(){
        User u = new User(1L, "John", "Smith", "john1", "pw", "USER", "john@mail.com", LocalDateTime.now(), new ArrayList<Book>(), new Cart());
        UserMeDTO dto = new UserMeDTO("John", "Smith", "john1","john@mail.com");

        when(userRepository.findByUsername("john1")).thenReturn(Optional.of(u));
        when(userMapper.toUserMeDTO(u)).thenReturn(dto);

        UserMeDTO result = userService.getUserDetails("john1");

        assertEquals("john1", result.getUsername());

    }

    @Test
    @WithMockUser(username="john", roles={"USER"})
    @DisplayName("Test getUserDetails - Not Found")
    void testGetUserDetailsNotFound(){
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserDetails("missing"));
    }

    @Test
    @DisplayName("Test getUsers() – Success")
    @WithMockUser(roles = "ADMIN")
    void testGetUsersSuccess() {
        User u1 = new User(1L, "John", "Smith", "john1", "pw", "USER", "john@mail.com", LocalDateTime.now(), new ArrayList<Book>(), new Cart());
        User u2 = new User(2L, "Anna", "White", "anna2", "pw", "ADMIN", "anna@mail.com", LocalDateTime.now(), new ArrayList<Book>(), new Cart());

        UserAdminDTO dto1 = new UserAdminDTO(1L, "John", "Smith", "john1","john@mail.com", "USER", LocalDateTime.now());
        UserAdminDTO dto2 = new UserAdminDTO(2L, "Anna", "White", "admin", "ADMIN", "anna@mail.com",LocalDateTime.now());

        when(userRepository.findAll()).thenReturn(Arrays.asList(u1, u2));
        when(userMapper.toAdminDTO(u1)).thenReturn(dto1);
        when(userMapper.toAdminDTO(u2)).thenReturn(dto2);

        List<UserAdminDTO> result = userService.getUsers();

        assertEquals(2, result.size());
        assertEquals("Anna", result.get(1).getFirstName());
    }

    @Test
    @DisplayName("Test getUserById – Success")
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() {
        User u = new User(1L, "John", "Smith", "john1", "pw", "USER", "john@mail.com", LocalDateTime.now(), new ArrayList<Book>(), new Cart());
        UserAdminDTO dto = new UserAdminDTO(1L, "John", "Smith", "john1", "john@mail.com","USER",LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(userMapper.toAdminDTO(u)).thenReturn(dto);

        UserAdminDTO result = userService.getUserById(1L);

        assertEquals("John", result.getFirstName());
    }

    @Test
    @DisplayName("Test getUserById – Not Found")
    @WithMockUser(roles = "ADMIN")
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1L));

        assertEquals("User with id 1 not found", ex.getMessage());
    }

    @Test
    @DisplayName("Test deleteUser – Success")
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser() {
        Long id = 1L;

        User user = new User();
        user.setId(id);

        Book book1 = new Book();
        book1.setId(100l);
        user.setFavoriteBooks(new ArrayList<>(List.of(book1)));

        when(userRepository.existsById(1L)).thenReturn(true);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        doNothing().when(userRepository).deleteById(id);

        userService.deleteUser(1L);
        assertTrue(user.getFavoriteBooks().isEmpty());
    }

    @Test
    @DisplayName("Test deleteUser – Not Found")
    @WithMockUser(roles = "ADMIN")
    void testDeleteUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(1L));

        assertEquals("User with id 1 not found", ex.getMessage());
    }

    @Test
    @DisplayName("getFavoriteBooks - success")
    @WithMockUser(roles = "USER")
    void testGetFavoriteBooksSuccess() {

        User user = new User();
        Book b1 = new Book();
        Book b2 = new Book();
        user.setFavoriteBooks(List.of(b1, b2));

        BookDTO d1 = new BookDTO();
        BookDTO d2 = new BookDTO();

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        when(bookMapper.toDTO(b1)).thenReturn(d1);
        when(bookMapper.toDTO(b2)).thenReturn(d2);

        List<BookDTO> result = userService.getFavoriteBooks("john");

        assertEquals(2, result.size());
        assertTrue(result.contains(d1));
        assertTrue(result.contains(d2));
    }

    @Test
    @DisplayName("getFavoriteBooks - user not found")
    @WithMockUser(roles = "USER")
    void testGetFavoriteBooksUserNotFound() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getFavoriteBooks("john"));
    }

    @Test
    @DisplayName("addFavoriteBook - success")
    @WithMockUser(roles = "USER")
    void testAddFavoriteBookSuccess() {

        User user = new User();
        user.setFavoriteBooks(new ArrayList<>());

        Book book = new Book();
        book.setId(10L);

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        user.setFavoriteBooks(List.of(book));
        userService.addFavoriteBook("john", 10L);

        assertEquals(1, user.getFavoriteBooks().size());
        assertTrue(user.getFavoriteBooks().contains(book));

    }

    @Test
    @DisplayName("addFavoriteBook - Failed")
    @WithMockUser(roles = "USER")
    void testAddFavoriteBookFailed() {

        User user = new User();
        user.setFavoriteBooks(new ArrayList<>());

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> userService.addFavoriteBook("john", 10L));

    }

    @Test
    @DisplayName("remove Favorite Book - Success")
    @WithMockUser(roles = "USER")
    void testRemoveFavoriteBook(){

        Book book = new Book();
        book.setId(1L);

        User user = new User();
        user.setUsername("john");
        user.setFavoriteBooks(new ArrayList<>(List.of(book)));

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        userService.removeFavoriteBook("john", 10L);

        assertTrue(user.getFavoriteBooks().isEmpty());

    }

    @Test
    @DisplayName("removeFavoriteBook - book not found")
    void testRemoveFavoriteBookBookNotFound() {

        User user = new User();
        user.setFavoriteBooks(new ArrayList<>());

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(bookRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.getBookById(10L));
    }


}

