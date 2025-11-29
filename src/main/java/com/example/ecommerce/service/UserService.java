package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.ChangePasswordRequest;
import com.example.ecommerce.dto.request.UpdateProfileRequest;
import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DatabaseClient databaseClient;

    public Mono<UserResponse> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userRepository.findById(userDetails.getId())
            .map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isEmailVerified(user.getIsEmailVerified())
                .roles(userDetails.getRoles())
                .build())
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")));
    }

    public Mono<UserResponse> updateProfile(Authentication authentication, UpdateProfileRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userRepository.findById(userDetails.getId())
            .flatMap(user -> {
                if (request.getFirstName() != null) {
                    user.setFirstName(request.getFirstName());
                }
                if (request.getLastName() != null) {
                    user.setLastName(request.getLastName());
                }
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            })
            .map(user -> UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isEmailVerified(user.getIsEmailVerified())
                .roles(userDetails.getRoles())
                .build())
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")));
    }

    public Mono<MessageResponse> changePassword(Authentication authentication, ChangePasswordRequest request) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userRepository.findById(userDetails.getId())
            .flatMap(user -> {
                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                    return Mono.error(new BadRequestException("Current password is incorrect"));
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            })
            .thenReturn(new MessageResponse("Password changed successfully"))
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")));
    }
}
