package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.CartDTO;
import com.bookstoreapp.springboot.book_store_app.dto.CartItemDTO;
import com.bookstoreapp.springboot.book_store_app.exception.BookNotFoundException;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.mapper.BookMapper;
import com.bookstoreapp.springboot.book_store_app.mapper.CartMapper;
import com.bookstoreapp.springboot.book_store_app.model.*;
import com.bookstoreapp.springboot.book_store_app.repository.BookRepository;
import com.bookstoreapp.springboot.book_store_app.repository.CartItemRepository;
import com.bookstoreapp.springboot.book_store_app.repository.CartRepository;
import com.bookstoreapp.springboot.book_store_app.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

	private CartRepository cartRepository;

    private CartItemRepository cartItemRepository;
    private BookRepository bookRepository;

	private UserRepository userRepository;

	private BookMapper bookMapper;

	private CartMapper cartMapper;


	public CartService(CartRepository cartRepository, BookRepository booksRepository, UserRepository userRepository
			, BookMapper bookMapper, CartMapper cartMapper, CartItemRepository cartItemRepository) {

		this.cartRepository = cartRepository;
		this.bookRepository = booksRepository;
		this.userRepository = userRepository;
		this.bookMapper = bookMapper;
		this.cartMapper = cartMapper;
        this.cartItemRepository = cartItemRepository;
	}

	public CartDTO getMyCart(String username){
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(username));

        Cart cart = user.getCart();
        CartDTO cartDTO = cartMapper.toDTO(cart);

        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(item -> item.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartDTO.setTotalAmount(totalAmount);

        return cartDTO;
	}

	public CartDTO addToCart(String username, Long bookId, int quantity){
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(username));

		Book book = bookRepository.findById(bookId)
				.orElseThrow(() -> new BookNotFoundException(bookId));

        if (book.getAvailable() == AvailabilityStatus.UNAVAILABLE) {
            throw new BookUnavailableException();
        }

        Cart cart = user.getCart();

        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setCart(cart);
        cartItem.setQuantity(quantity);

        cart.addCartItem(cartItem);

        cartRepository.save(cart);

        return cartMapper.toDTO(cart);
	}

	public CartDTO removeFromCart(String username, Long bookId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        CartItem cartItem = cartItemRepository
                .findCartItemByCartIdAndBookId(user.getCart().getId(), bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        Cart cart = user.getCart();
        cart.removeCartItem(cartItem);

        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toDTO(savedCart);

    }

    public CartDTO updateCartItemQuantity(String username, Long bookId, int quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Cart cart = user.getCart();
        CartItem cartItem = cartItemRepository
                .findCartItemByCartIdAndBookId(user.getCart().getId(), bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if(quantity > 0){
            cartItem.setQuantity(quantity);
        }else{
            cart.removeCartItem(cartItem);
        }

        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toDTO(savedCart);

    }
}
