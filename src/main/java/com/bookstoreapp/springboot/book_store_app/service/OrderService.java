package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.AdminOrderDTO;
import com.bookstoreapp.springboot.book_store_app.dto.OrderDTO;
import com.bookstoreapp.springboot.book_store_app.exception.CartIsEmptyException;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.mapper.AdminOrderMapper;
import com.bookstoreapp.springboot.book_store_app.mapper.OrderMapper;
import com.bookstoreapp.springboot.book_store_app.model.*;
import com.bookstoreapp.springboot.book_store_app.repository.BookRepository;
import com.bookstoreapp.springboot.book_store_app.repository.OrderItemRepository;
import com.bookstoreapp.springboot.book_store_app.repository.OrderRepository;
import com.bookstoreapp.springboot.book_store_app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private BookRepository bookRepository;
    private UserRepository userRepository;
    private OrderMapper orderMapper;
    private AdminOrderMapper adminOrderMapper;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository
            , BookRepository bookRepository, UserRepository userRepository,
                        OrderMapper orderMapper, AdminOrderMapper adminOrderMapper){
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
        this.adminOrderMapper = adminOrderMapper;

    }

    public List<AdminOrderDTO> getAllOrdersForAdmin() {
        return orderRepository.findAll()
                .stream()
                .map(adminOrderMapper::toDTO)
                .toList();
    }

    public OrderDTO placeOrder(String username){

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Cart cart = user.getCart();
        if (cart.getCartItems().isEmpty()){
            throw  new CartIsEmptyException();
        }

        Order order = new Order();
        order.setUser(user);

        BigDecimal total = BigDecimal.ZERO;
        for(CartItem cartItem : cart.getCartItems()){

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getBook().getPrice());

            order.getOrderItems().add(orderItem);

            total = total.add(
                    cartItem.getBook().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        order.setTotalAmount(total);
        cart.getCartItems().clear();

        return orderMapper.toDTO(orderRepository.save(order));
    }

    public List<OrderDTO> getMyOrders(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        List<Order> orders = orderRepository.findAllByUserId(user.getId());


        return orders.stream().map(orderMapper::toDTO).collect(Collectors.toList());
    }

}
