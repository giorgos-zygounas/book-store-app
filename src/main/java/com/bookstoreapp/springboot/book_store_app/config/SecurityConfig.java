package com.bookstoreapp.springboot.book_store_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authReq ->{
                    authReq
                            .requestMatchers("/users/login", "/users/register").permitAll()
                            .requestMatchers("/users/me").hasRole("USER")
                            .requestMatchers("/users/me/favorites").hasRole("USER")
                            .requestMatchers("/users/me/favorites/**").hasRole("USER")
                            // Μόνο ADMIN για λίστα και details
                            .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/books").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.POST, "/books").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/books").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET,"/users/me/favorites").hasRole("USER")
                            .requestMatchers(HttpMethod.POST,"/users/me/favorites/**").hasRole("USER")
                            .anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
