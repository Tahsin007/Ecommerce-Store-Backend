package com.example.ecommerce.controller;
import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.dto.response.UserResponse;
import com.example.ecommerce.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public Flux<UserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public Mono<UserResponse> getUserById(@PathVariable Long id) {
        return adminService.getUserById(id);
    }

    @PostMapping("/users/{userId}/roles/{roleName}")
    public Mono<MessageResponse> assignRoleToUser(
        @PathVariable Long userId,
        @PathVariable String roleName) {
        return adminService.assignRoleToUser(userId, roleName);
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public Mono<MessageResponse> removeRoleFromUser(
        @PathVariable Long userId,
        @PathVariable String roleName) {
        return adminService.removeRoleFromUser(userId, roleName);
    }

    @PutMapping("/users/{userId}/status")
    public Mono<MessageResponse> toggleUserStatus(@PathVariable Long userId) {
        return adminService.toggleUserStatus(userId);
    }
}
