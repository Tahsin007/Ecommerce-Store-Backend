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

    /**
     * Find verification token by token string
     * @param token the verification token string
     * @return Mono of VerificationToken
     */
    Mono<VerificationToken> findByToken(String token);

    /**
     * Find all verification tokens for a user
     * @param userId the user ID
     * @return Flux of VerificationToken
     */
    Flux<VerificationToken> findByUserId(Long userId);

    /**
     * Find verification token by user ID and token type
     * @param userId the user ID
     * @param tokenType the token type (e.g., "EMAIL_VERIFICATION", "PASSWORD_RESET")
     * @return Mono of VerificationToken
     */
    @Query("SELECT * FROM verification_tokens WHERE user_id = :userId AND token_type = :tokenType")
    Mono<VerificationToken> findByUserIdAndTokenType(Long userId, String tokenType);

    /**
     * Delete verification token by user ID
     * @param userId the user ID
     * @return Mono of Void
     */
    @Modifying
    @Query("DELETE FROM verification_tokens WHERE user_id = :userId")
    Mono<Void> deleteByUserId(Long userId);

    /**
     * Delete verification token by token string
     * @param token the verification token string
     * @return Mono of Void
     */
    @Modifying
    @Query("DELETE FROM verification_tokens WHERE token = :token")
    Mono<Void> deleteByToken(String token);

    /**
     * Delete verification tokens by user ID and token type
     * @param userId the user ID
     * @param tokenType the token type
     * @return Mono of Void
     */
    @Modifying
    @Query("DELETE FROM verification_tokens WHERE user_id = :userId AND token_type = :tokenType")
    Mono<Void> deleteByUserIdAndTokenType(Long userId, String tokenType);

    /**
     * Delete expired verification tokens
     * @return Mono of Void
     */
    @Modifying
    @Query("DELETE FROM verification_tokens WHERE expiry_date < CURRENT_TIMESTAMP")
    Mono<Void> deleteExpiredTokens();

    /**
     * Check if token exists
     * @param token the verification token string
     * @return Mono of Boolean
     */
    Mono<Boolean> existsByToken(String token);
}
