package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.dto.CartDTO;
import com.bookstoreapp.springboot.book_store_app.dto.CartRequest;
import com.bookstoreapp.springboot.book_store_app.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/users/me/carts/items")
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService){
        this.cartService = cartService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public CartDTO getMyCart(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartService.getMyCart(username);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> addToCart(@RequestBody CartRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CartDTO updatedCart = cartService.addToCart(username, request.getBookId(), request.getQuantity());
        return ResponseEntity.ok("Book added to cart successfully");
    }

    @DeleteMapping(path = "/{book_id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> removeFromCart(@PathVariable Long book_id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CartDTO updatedCart = cartService.removeFromCart(username, book_id);
        return ResponseEntity.ok("Book removed from cart successfully");
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> updateCartItemQuantity(@RequestBody CartRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CartDTO updatedCart = cartService.updateCartItemQuantity(username, request.getBookId(), request.getQuantity());
        return ResponseEntity.ok("Cart updated Successfully");
    }

}
