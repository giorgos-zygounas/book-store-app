package com.bookstoreapp.springboot.book_store_app.service;

import com.bookstoreapp.springboot.book_store_app.dto.UserAdminDTO;
import com.bookstoreapp.springboot.book_store_app.dto.UserMeDTO;
import com.bookstoreapp.springboot.book_store_app.exception.UserNotFoundException;
import com.bookstoreapp.springboot.book_store_app.mapper.UserMapper;
import com.bookstoreapp.springboot.book_store_app.model.AuthenticatedUser;
import com.bookstoreapp.springboot.book_store_app.model.User;
import com.bookstoreapp.springboot.book_store_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper){
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public User register(UserMeDTO userDTO){
        System.out.println("Request body :" + userDTO);

        User u = new User();
        u.setFirstName(userDTO.getFirstName());
        u.setLastName(userDTO.getLastName());
        u.setUsername(userDTO.getUsername());
        u.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        u.setRole("USER");
        u.setEmail(userDTO.getEmail());
        u.setCreated_at(LocalDateTime.now());

        return userRepository.save(u);

    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserAdminDTO> getUsers(){
        var result = userRepository.findAll()
                .stream()
                .map(userMapper::toAdminDTO).
                toList();
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserAdminDTO getUserById(Long id){
        return userRepository.findById(id)
                .map(userMapper::toAdminDTO)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id){
        if(!userRepository.existsById(id)){
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    // "/me"
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new AuthenticatedUser(u);


    }

    @PreAuthorize("hasRole('USER')")
    public UserMeDTO getUserDetails(String username){

        return userRepository.findByUsername(username)
                .map(userMapper::toUserMeDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));

    }
}
