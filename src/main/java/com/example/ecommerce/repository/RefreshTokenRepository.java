package com.example.ecommerce.repository;

import com.example.ecommerce.entity.RefreshToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface RefreshTokenRepository extends R2dbcRepository<RefreshToken,Long> {
    Mono<Void> deleteByUserId(Long userId);
    Mono<RefreshToken> findByToken(String token);
    Mono<Void> deleteByToken(String token);
}
