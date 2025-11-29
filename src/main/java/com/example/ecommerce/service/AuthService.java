package com.example.ecommerce.service;
import com.example.ecommerce.dto.request.*;
import com.example.ecommerce.dto.response.AuthResponse;
import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.exception.UnauthorizedException;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.CustomUserDetails;
import com.example.ecommerce.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final DatabaseClient databaseClient;

    public Mono<MessageResponse> signup(SignupRequest request) {
        return userRepository.existsByUsername(request.getUsername())
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new BadRequestException("Username already exists"));
                }
                return userRepository.existsByEmail(request.getEmail());
            })
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new BadRequestException("Email already exists"));
                }

                User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .isEnabled(true)
                    .isEmailVerified(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

                return userRepository.save(user);
            })
            .flatMap(savedUser -> assignDefaultRole(savedUser.getId())
                .then(verificationTokenService.createVerificationToken(savedUser, "EMAIL_VERIFICATION"))
                .flatMap(token -> emailService.sendVerificationEmail(savedUser.getEmail(), token))
                .thenReturn(new MessageResponse("User registered successfully. Please check your email to verify your account.")))
            .onErrorResume(e -> {
                if (e instanceof BadRequestException) {
                    return Mono.error(e);
                }
                log.error("Error during signup", e);
                return Mono.error(new RuntimeException("Registration failed"));
            });
    }

    public Mono<AuthResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsernameOrEmail())
            .switchIfEmpty(userRepository.findByEmail(request.getUsernameOrEmail()))
            .switchIfEmpty(Mono.error(new UnauthorizedException("Invalid credentials")))
            .flatMap(user -> {
                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    return Mono.error(new UnauthorizedException("Invalid credentials"));
                }
                if (!user.getIsEnabled()) {
                    return Mono.error(new UnauthorizedException("Account is disabled"));
                }
                return getUserRoles(user.getId())
                    .collectList()
                    .flatMap(roles -> {
                        CustomUserDetails userDetails = new CustomUserDetails(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getPassword(),
                            user.getIsEnabled(),
                            roles
                        );

                        String accessToken = tokenProvider.generateAccessToken(userDetails);
                        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

                        return refreshTokenService.saveRefreshToken(user.getId(), refreshToken)
                            .then(Mono.just(AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .expiresIn(tokenProvider.getAccessTokenExpiration())
                                .user(UserResponse.builder()
                                    .id(user.getId())
                                    .username(user.getUsername())
                                    .email(user.getEmail())
                                    .firstName(user.getFirstName())
                                    .lastName(user.getLastName())
                                    .isEmailVerified(user.getIsEmailVerified())
                                    .roles(roles)
                                    .build())
                                .build()));
                    });
            });
    }

    public Mono<AuthResponse> refreshToken(RefreshTokenRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
            .switchIfEmpty(Mono.error(new UnauthorizedException("Invalid refresh token")))
            .flatMap(refreshToken -> {
                if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                    return refreshTokenService.deleteByToken(request.getRefreshToken())
                        .then(Mono.error(new UnauthorizedException("Refresh token expired")));
                }

                return userRepository.findById(refreshToken.getUserId())
                    .flatMap(user -> getUserRoles(user.getId())
                        .collectList()
                        .map(roles -> {
                            CustomUserDetails userDetails = new CustomUserDetails(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getPassword(),
                                user.getIsEnabled(),
                                roles
                            );

                            String newAccessToken = tokenProvider.generateAccessToken(userDetails);

                            return AuthResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(refreshToken.getToken())
                                .tokenType("Bearer")
                                .expiresIn(tokenProvider.getAccessTokenExpiration())
                                .user(UserResponse.builder()
                                    .id(user.getId())
                                    .username(user.getUsername())
                                    .email(user.getEmail())
                                    .firstName(user.getFirstName())
                                    .lastName(user.getLastName())
                                    .isEmailVerified(user.getIsEmailVerified())
                                    .roles(roles)
                                    .build())
                                .build();
                        }));
            });
    }

    public Mono<MessageResponse> verifyEmail(VerifyEmailRequest request) {
        return verificationTokenService.findByToken(request.getToken())
            .switchIfEmpty(Mono.error(new BadRequestException("Invalid verification token")))
            .flatMap(token -> {
                if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
                    return Mono.error(new BadRequestException("Verification token expired"));
                }

                return userRepository.findById(token.getUserId())
                    .flatMap(user -> {
                        user.setIsEmailVerified(true);
                        user.setUpdatedAt(LocalDateTime.now());
                        return userRepository.save(user);
                    })
                    .then(verificationTokenService.deleteByUserId(token.getUserId()))
                    .thenReturn(new MessageResponse("Email verified successfully"));
            });
    }

    public Mono<MessageResponse> forgotPassword(ForgotPasswordRequest request) {
        return userRepository.findByEmail(request.getEmail())
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found with email: " + request.getEmail())))
            .flatMap(user -> verificationTokenService.createVerificationToken(user, "PASSWORD_RESET")
                .flatMap(token -> emailService.sendPasswordResetEmail(user.getEmail(), token))
                .thenReturn(new MessageResponse("Password reset link sent to your email")));
    }

    public Mono<MessageResponse> resetPassword(ResetPasswordRequest request) {
        return verificationTokenService.findByToken(request.getToken())
            .switchIfEmpty(Mono.error(new BadRequestException("Invalid reset token")))
            .flatMap(token -> {
                if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
                    return Mono.error(new BadRequestException("Reset token expired"));
                }

                return userRepository.findById(token.getUserId())
                    .flatMap(user -> {
                        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                        user.setUpdatedAt(LocalDateTime.now());
                        return userRepository.save(user);
                    })
                    .then(verificationTokenService.deleteByUserId(token.getUserId()))
                    .thenReturn(new MessageResponse("Password reset successfully"));
            });
    }

    private Mono<Void> assignDefaultRole(Long userId) {
        String sql = """
            INSERT INTO user_roles (user_id, role_id)
            SELECT :userId, id FROM roles WHERE name = 'ROLE_USER'
            """;

        return databaseClient.sql(sql)
            .bind("userId", userId)
            .then();
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
