package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.dto.AdminOrderDTO;
import com.bookstoreapp.springboot.book_store_app.dto.BookDTO;
import com.bookstoreapp.springboot.book_store_app.dto.OrderDTO;
import com.bookstoreapp.springboot.book_store_app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/users/me/orders")
public class OrderController {

    private OrderService orderService;

    public OrderController( OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<OrderDTO>> getMyOrders() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<OrderDTO> orderDTO = orderService.getMyOrders(username);
        return ResponseEntity.ok(orderDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> placeOrder() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        OrderDTO orderDTO = orderService.placeOrder(username);
        return ResponseEntity.ok("Order placed successfully!");
    }
}