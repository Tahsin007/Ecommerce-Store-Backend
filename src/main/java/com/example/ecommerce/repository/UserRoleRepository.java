package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Role;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRoleRepository extends R2dbcRepository<Role, Long> {

    @Query("SELECT * FROM user_roles WHERE role_id = :roleId")
    Flux<Role> findByRoleId(Long roleId);

    @Modifying
    @Query("DELETE FROM user_roles WHERE user_id = :userId")
    Mono<Void> deleteByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM user_roles WHERE user_id = :userId AND role_id = :roleId")
    Mono<Void> deleteByUserIdAndRoleId(Long userId, Long roleId);

    @Query("SELECT COUNT(*) > 0 FROM user_roles WHERE user_id = :userId AND role_id = :roleId")
    Mono<Boolean> existsByUserIdAndRoleId(Long userId, Long roleId);

    @Modifying
    @Query("INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)")
    Mono<Void> insertUserRole(Long userId, Long roleId);

    Mono<Role> findByName(String name);

    @Query("SELECT r.name FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :userId")
    Flux<String> findRoleNamesByUserId(Long userId);

    @Modifying
    @Query("INSERT INTO user_roles (user_id, role_id) SELECT :userId, id FROM roles WHERE name = 'ROLE_USER'")
    Mono<Void> assignDefaultRole(Long userId);
}
