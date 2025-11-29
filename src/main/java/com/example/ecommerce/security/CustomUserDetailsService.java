package com.example.ecommerce.security;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;
    private final DatabaseClient databaseClient;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
            .switchIfEmpty(userRepository.findByEmail(username))
            .flatMap(user -> getUserRoles(user.getId())
                .collectList()
                .map(roles -> new CustomUserDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    user.getIsEnabled(),
                    roles
                )))
            .cast(UserDetails.class)
            .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)));
    }

    private reactor.core.publisher.Flux<String> getUserRoles(Long userId) {
        String query = """
            SELECT r.name FROM roles r
            JOIN user_roles ur ON r.id = ur.role_id
            WHERE ur.user_id = :userId
            """;

        return databaseClient.sql(query)
            .bind("userId", userId)
            .map(row -> row.get("name", String.class))
            .all();
    }
}