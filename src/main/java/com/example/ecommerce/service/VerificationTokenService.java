package com.example.ecommerce.service;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.entity.VerificationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;

    @Value("${verification.token-expiration}")
    private Long tokenExpiration;

    public Mono<String> createVerificationToken(User user, String tokenType) {
        String token = UUID.randomUUID().toString();

        return tokenRepository.deleteByUserId(user.getId())
            .then(Mono.defer(() -> {
                VerificationToken verificationToken = VerificationToken.builder()
                    .token(token)
                    .userId(user.getId())
                    .tokenType(tokenType)
                    .expiryDate(LocalDateTime.now().plusSeconds(tokenExpiration / 1000))
                    .createdAt(LocalDateTime.now())
                    .build();

                return tokenRepository.save(verificationToken)
                    .map(VerificationToken::getToken);
            }));
    }

    public Mono<VerificationToken> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public Mono<Void> deleteByUserId(Long userId) {
        return tokenRepository.deleteByUserId(userId);
    }
}
