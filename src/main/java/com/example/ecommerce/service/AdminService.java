package com.example.ecommerce.service;
import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.exception.BadRequestException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserRoleRepository roleRepository;

    public Flux<UserResponse> getAllUsers() {
        return userRepository.findAll()
            .flatMap(user -> roleRepository.findRoleNamesByUserId(user.getId())
                .collectList()
                .map(roles -> UserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .isEmailVerified(user.getIsEmailVerified())
                    .roles(roles)
                    .build()));
    }

    public Mono<UserResponse> getUserById(Long userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "id", userId)))
            .flatMap(user -> roleRepository.findRoleNamesByUserId(user.getId())
                .collectList()
                .map(roles -> UserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .isEmailVerified(user.getIsEmailVerified())
                    .roles(roles)
                    .build()));
    }
    
    public Mono<MessageResponse> assignRoleToUser(Long userId, String roleName) {
        String roleNameWithPrefix = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "id", userId)))
            .flatMap(user -> roleRepository.findByName(roleNameWithPrefix)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Role not found: " + roleNameWithPrefix)))
                .flatMap(role -> roleRepository.existsByUserIdAndRoleId(userId, role.getId())
                    .flatMap(hasRole -> {
                        if (hasRole) {
                            return Mono.<MessageResponse>error(new BadRequestException("User already has this role"));
                        }
                        return roleRepository.insertUserRole(userId, role.getId())
                                .thenReturn(new MessageResponse("Role assigned successfully"));
                    })));
    }

    public Mono<MessageResponse> removeRoleFromUser(Long userId, String roleName) {
        // Ensure role name has ROLE_ prefix
        String roleNameWithPrefix = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;

        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "id", userId)))
            .flatMap(user -> roleRepository.findByName(roleNameWithPrefix)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Role not found: " + roleNameWithPrefix)))
                .flatMap(role -> roleRepository.deleteByUserIdAndRoleId(userId, role.getId())
                    .thenReturn(new MessageResponse("Role removed successfully"))));
    }

    public Mono<MessageResponse> toggleUserStatus(Long userId) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "id", userId)))
            .flatMap(user -> {
                user.setIsEnabled(!user.getIsEnabled());
                user.setUpdatedAt(LocalDateTime.now());
                return userRepository.save(user);
            })
            .map(user -> new MessageResponse(
                "User " + (user.getIsEnabled() ? "enabled" : "disabled") + " successfully"));
    }
}
