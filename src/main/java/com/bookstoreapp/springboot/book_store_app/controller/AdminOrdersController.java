package com.bookstoreapp.springboot.book_store_app.controller;

import com.bookstoreapp.springboot.book_store_app.dto.AdminOrderDTO;
import com.bookstoreapp.springboot.book_store_app.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/orders")
public class AdminOrdersController {

    private OrderService orderService;

    public AdminOrdersController( OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminOrderDTO>> getAllOrders() {

        List<AdminOrderDTO> adminOrderDTO = orderService.getAllOrdersForAdmin();
        return ResponseEntity.ok(adminOrderDTO);
    }
}
