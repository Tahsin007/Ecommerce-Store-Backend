package com.example.ecommerce.repository;

import com.example.ecommerce.entity.VerificationToken;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VerificationTokenRepository extends R2dbcRepository<VerificationToken, Long> {
    Mono<VerificationToken> findByToken(String token);

    Flux<VerificationToken> findByUserId(Long userId);
    @Query("SELECT * FROM verification_tokens WHERE user_id = :userId AND token_type = :tokenType")
    Mono<VerificationToken> findByUserIdAndTokenType(Long userId, String tokenType);

    @Modifying
    @Query("DELETE FROM verification_tokens WHERE user_id = :userId")
    Mono<Void> deleteByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM verification_tokens WHERE token = :token")
    Mono<Void> deleteByToken(String token);

    @Modifying
    @Query("DELETE FROM verification_tokens WHERE user_id = :userId AND token_type = :tokenType")
    Mono<Void> deleteByUserIdAndTokenType(Long userId, String tokenType);

    @Modifying
    @Query("DELETE FROM verification_tokens WHERE expiry_date < CURRENT_TIMESTAMP")
    Mono<Void> deleteExpiredTokens();

    Mono<Boolean> existsByToken(String token);
}
