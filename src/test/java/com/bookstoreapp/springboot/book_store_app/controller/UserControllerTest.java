package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.config.SecurityConfig;
import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.dto.UserAdminDTO;
import com.bookstoreapp.springboot.book_store_app.dto.UserMeDTO;
import com.bookstoreapp.springboot.book_store_app.exception.BookNotFoundException;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.mapper.BookMapper;
import com.bookstoreapp.springboot.book_store_app.model.Book;
import com.bookstoreapp.springboot.book_store_app.model.User;
import com.bookstoreapp.springboot.book_store_app.repository.UserRepository;
import com.bookstoreapp.springboot.book_store_app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@Import(SecurityConfig.class)
@EnableMethodSecurity
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    // -------- Helper JSON Serializer --------
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Register user - Success")
    void testRegisterUser() throws Exception {
        UserMeDTO dto = new UserMeDTO("John", "Doe", "john", "1234", "john@test.com");

        when(userService.register(any(UserMeDTO.class))).thenReturn(new User());

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User created successfully"));
    }

    @Test
    @DisplayName("Register user - service throws exception")
    void testRegisterUserThrowsException() throws Exception {
        UserMeDTO dto = new UserMeDTO("John", "Doe", "john", "1234", "john@test.com");

        when(userService.register(any(UserMeDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Unexpected error occurred")));
    }

    @Test
    @DisplayName("Get users as ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUsersAsAdmin() throws Exception {
        List<UserAdminDTO> users = List.of(
                new UserAdminDTO(1L, "John", "Doe", "john", "john@example.com", "USER", LocalDateTime.now())
        );

        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("john")));
    }

    @Test
    @DisplayName("Get users as USER - forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetUsersForbidden() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get user by id as ADMIN - Success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserByIdSuccess() throws Exception {

        UserAdminDTO dto = new UserAdminDTO(1L, "John", "Doe", "john", "john@example.com", "USER", LocalDateTime.now());

        when(userService.getUserById(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")));
    }

    @Test
    @DisplayName("Get user by id as ADMIN - Not Found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserByIdNotFound() throws Exception {

        doThrow(new UserNotFoundException(1L)).when(userService).getUserById(1L);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get user by id as USER - forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetUserByIdForbidden() throws Exception {
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete user as ADMIN - success")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUserSuccess() throws Exception {

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Delete user as ADMIN - not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUserNotFound() throws Exception {

        doThrow(new UserNotFoundException(1L)).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete user as USER - forbidden")
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteUserForbidden() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get /me as authenticated USER")
    @WithMockUser(username = "john", roles = {"USER"})
    void testGetMeSuccess() throws Exception {

        UserMeDTO dto = new UserMeDTO("John", "Doe", "john", null, "john@test.com");

        when(userService.getUserDetails("john")).thenReturn(dto);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.email", is("john@test.com")));
    }

    @Test
    @DisplayName("/me returns null")
    @WithMockUser(username = "john", roles = {"USER"})
    void testGetMeNull() throws Exception {

        when(userService.getUserDetails("john")).thenReturn(null);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found with username: john")));
    }

    @Test
    @DisplayName("/me unauthenticated ")
    void testGetMeUnauthenticated() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("Test get favorite books - Success")
    @WithMockUser(username = "john", roles = {"USER"})
    void testGetFavoriteBooksSuccess() throws Exception {

        User user = new User();
        user.setUsername("john");

        List<BookDTO> favorites = List.of(
                new BookDTO("Title 1", "Author 1", "Good book", new BigDecimal("10.99"), true),
                new BookDTO("Title 2", "Author 2", "Nice book", new BigDecimal("20.99"), true)
        );

        when(userService.getFavoriteBooks("john")).thenReturn(favorites);

        mockMvc.perform(get("/users/me/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Title 1"))
                .andExpect(jsonPath("$[0].author").value("Author 1"))
                .andExpect(jsonPath("$[0].description").value("Good book"))
                .andExpect(jsonPath("$[1].title").value("Title 2"))
                .andExpect(jsonPath("$[1].author").value("Author 2"))
                .andExpect(jsonPath("$[1].description").value("Nice book"));
    }

    @Test
    @DisplayName("Test get favorite books - Failed")
    @WithMockUser(username = "john", roles = {"USER"})
    void testGetFavoriteBooksFailed() throws Exception{
        User user = new User();

        doThrow(new UserNotFoundException("john")).when(userService)
                .getFavoriteBooks("john");

        mockMvc.perform(get("/users/me/favorites"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User not found with username: john"));
    }

    @Test
    @DisplayName("Remove book from favorites - success")
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteBookFromFavoritesSuccess() throws Exception {

        User user = new User();
        user.setUsername("john");

        doNothing().when(userService).removeFavoriteBook(user.getUsername(), 1L);

        mockMvc.perform(delete("/users/me/favorites/{book_id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Remove book from favorites - not found")
    @WithMockUser(username = "john", roles = {"USER"})
    void testRemoveBookFromFavoritesNotFound() throws Exception {

        doThrow(new BookNotFoundException(1L))
                .when(userService).removeFavoriteBook("john", 1L);

        mockMvc.perform(delete("/users/me/favorites/{book_id}", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book with id 1 not found"));
    }
}

