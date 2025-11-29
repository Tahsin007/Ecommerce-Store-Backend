package com.example.ecommerce.repository;

import com.example.ecommerce.entity.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

    /**
     * Find user by username
     * @param username the username
     * @return Mono of User
     */
    Mono<User> findByUsername(String username);

    /**
     * Find user by email
     * @param email the email
     * @return Mono of User
     */
    Mono<User> findByEmail(String email);

    /**
     * Check if username exists
     * @param username the username
     * @return Mono of Boolean
     */
    Mono<Boolean> existsByUsername(String username);

    /**
     * Check if email exists
     * @param email the email
     * @return Mono of Boolean
     */
    Mono<Boolean> existsByEmail(String email);

    /**
     * Find user by username or email
     * @param username the username
     * @param email the email
     * @return Mono of User
     */
    @Query("SELECT * FROM users WHERE username = :username OR email = :email")
    Mono<User> findByUsernameOrEmail(String username, String email);
}
