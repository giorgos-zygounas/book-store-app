package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.config.SecurityConfig;
import com.bookstoreapp.springboot.book_store_app.dto.UserAdminDTO;
import com.bookstoreapp.springboot.book_store_app.dto.UserMeDTO;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.model.User;
import com.bookstoreapp.springboot.book_store_app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    // -------------------------------
    // Helper to convert object to JSON
    // -------------------------------
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -------------------------------
    // Helper to mock authenticated user with roles
    // -------------------------------
    private void mockUser(String username, String... roles) {
        SecurityContext context = mock(SecurityContext.class);
        List<SimpleGrantedAuthority> authorities = Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .toList();
        when(context.getAuthentication())
                .thenReturn(new UsernamePasswordAuthenticationToken(username, "password", authorities));
        SecurityContextHolder.setContext(context);
    }

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // -------------------------------
    // REGISTER USER (no role required)
    // -------------------------------
    @Test
    void testRegisterUser() throws Exception {
        UserMeDTO userMeDTO = new UserMeDTO("John", "Doe", "john", "1234", "john@test.com");

        when(userService.register(any(UserMeDTO.class))).thenReturn(new User());

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userMeDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("User created successfully"));
    }



    @Test
    @DisplayName("Register user throws exception")
    void testRegisterUserThrowsException() throws Exception {
        UserMeDTO userMeDTO = new UserMeDTO("John", "Doe", "john", "1234", "john@test.com");

        when(userService.register(any(UserMeDTO.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userMeDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", is("Unexpected error occurred")));
    }


    // -------------------------------
    // GET ALL USERS (ADMIN only)
    // -------------------------------
    @Test
    void testGetUsersAsAdmin() throws Exception {
        List<UserAdminDTO> users = List.of(new UserAdminDTO(1L, "John", "Doe", "john", "john@example.com", "USER", LocalDateTime.now()));

        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .with(user("admin").roles("ADMIN"))) // <-- admin user
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].username", is("john")));
    }

    @Test
    @DisplayName("Get all users as USER forbidden")
    void testGetUsersAsUserForbidden() throws Exception {
        mockUser("user", "ROLE_USER");

        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    // -------------------------------
    // GET USER BY ID (ADMIN only)
    // -------------------------------
    @Test
    @DisplayName("Get user by id as ADMIN")
    void testGetUserByIdSuccess() throws Exception {
        mockUser("admin", "ROLE_ADMIN");

       UserAdminDTO dto =  new UserAdminDTO(1L, "John", "Doe"
                , "john", "john@example.com"
                ,"USER" ,LocalDateTime.now());
        when(userService.getUserById(1L)).thenReturn(dto);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")));
    }

    @Test
    @DisplayName("Get user by id as ADMIN not found")
    void testGetUserByIdNotFound() throws Exception {
        mockUser("admin", "ROLE_ADMIN");

        doThrow(new UserNotFoundException(1L)).when(userService).getUserById(1L);

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get user by id as USER forbidden")
    void testGetUserByIdAsUserForbidden() throws Exception {
        mockUser("user", "ROLE_USER");

        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isForbidden());
    }

    // -------------------------------
    // DELETE USER (ADMIN only)
    // -------------------------------
    @Test
    @DisplayName("Delete user as ADMIN")
    void testDeleteUserSuccess() throws Exception {
        mockUser("admin", "ROLE_ADMIN");

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Delete user as ADMIN not found")
    void testDeleteUserNotFound() throws Exception {
        mockUser("admin", "ROLE_ADMIN");

        doThrow(new UserNotFoundException(1L)).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete user as USER forbidden")
    void testDeleteUserAsUserForbidden() throws Exception {
        mockUser("user", "ROLE_USER");

        mockMvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isForbidden());
    }

    // -------------------------------
    // GET /me (authenticated users)
    // -------------------------------
    @Test
    void testGetUserDetails() throws Exception {
        UserMeDTO mockDTO = new UserMeDTO("John", "Doe", "john", null, "john@test.com");

        when(userService.getUserDetails("john")).thenReturn(mockDTO);

        mockMvc.perform(get("/users/me")
                        .with(user("john").roles("USER"))) // <-- authenticated user
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")))
                .andExpect(jsonPath("$.email", is("john@test.com")));
    }



    @Test
    @DisplayName("/users/me returns null")
    void testGetUserDetailsReturnsNull() throws Exception {
        when(userService.getUserDetails("john")).thenReturn(null);

        mockMvc.perform(get("/users/me")
                        .with(user("john").roles("USER")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("User not found with username: john")));
    }


    @Test
    @DisplayName("Get /me unauthenticated should fail")
    void testGetUserDetailsUnauthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
