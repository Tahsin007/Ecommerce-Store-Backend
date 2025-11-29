package com.example.ecommerce.service;
import com.example.ecommerce.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    public Mono<RefreshToken> saveRefreshToken(Long userId, String token) {
        return refreshTokenRepository.deleteByUserId(userId)
            .then(Mono.defer(() -> {
                RefreshToken refreshToken = RefreshToken.builder()
                    .token(token)
                    .userId(userId)
                    .expiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                    .createdAt(LocalDateTime.now())
                    .build();
                return refreshTokenRepository.save(refreshToken);
            }));
    }

    public Mono<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Mono<Void> deleteByToken(String token) {
        return refreshTokenRepository.deleteByToken(token);
    }

    public Mono<Void> deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUserId(userId);
    }
}
