📚 Online Bookstore API

A RESTful Online Bookstore API built with Spring Boot, featuring user authentication, role-based authorization, shopping cart, favorites, and order management.
- Features
  - Users

User registration & login

Role-based access (USER, ADMIN)

View & update own profile (/users/me)

Favorite books management

📖 Books

Create, update, delete books (ADMIN only)

View available books

🛒 Cart

Add books to cart

Update item quantities

Remove items from cart

View cart with total amount
(Cart is user-based via /users/me/carts)

❤️ Favorites

Add book to favorites

Remove book from favorites

View favorite books

📦 Orders

Place order from cart

View own orders

Admin view of all orders, including which user placed each order

🔐 Security

Spring Security

HTTP Basic Authentication

Method-level security with @PreAuthorize

Role-based access control

Secure /users/me/** endpoints (no user ID exposure)

🧱 Tech Stack

Java 17

Spring Boot

Spring Security

Spring Data JPA (Hibernate)

H2 / PostgreSQL (configurable)

Maven

OpenAPI 3 (Swagger)

JUnit 5 & Mockito
