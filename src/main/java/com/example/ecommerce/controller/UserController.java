package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.ChangePasswordRequest;
import com.example.ecommerce.dto.request.UpdateProfileRequest;
import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Mono<UserResponse> getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public Mono<UserResponse> updateProfile(
        Authentication authentication,
        @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(authentication, request);
    }

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public Mono<MessageResponse> changePassword(
        Authentication authentication,
        @Valid @RequestBody ChangePasswordRequest request) {
        return userService.changePassword(authentication, request);
    }
}

