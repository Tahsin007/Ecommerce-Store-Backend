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

    /**
     * Find all user-role mappings by user ID
     * @param userId the user ID
     * @return Flux of UserRole
     */
    @Query("SELECT * FROM user_roles WHERE user_id = :userId")
    Flux<Role> findByUserId(Long userId);

    /**
     * Find all user-role mappings by role ID
     * @param roleId the role ID
     * @return Flux of UserRole
     */
    @Query("SELECT * FROM user_roles WHERE role_id = :roleId")
    Flux<Role> findByRoleId(Long roleId);

    /**
     * Delete all roles for a user
     * @param userId the user ID
     * @return Mono of Void
     */
    @Modifying
    @Query("DELETE FROM user_roles WHERE user_id = :userId")
    Mono<Void> deleteByUserId(Long userId);

    /**
     * Delete specific user-role mapping
     * @param userId the user ID
     * @param roleId the role ID
     * @return Mono of Void
     */
    @Modifying
    @Query("DELETE FROM user_roles WHERE user_id = :userId AND role_id = :roleId")
    Mono<Void> deleteByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * Check if user has a specific role
     * @param userId the user ID
     * @param roleId the role ID
     * @return Mono of Boolean
     */
    @Query("SELECT COUNT(*) > 0 FROM user_roles WHERE user_id = :userId AND role_id = :roleId")
    Mono<Boolean> existsByUserIdAndRoleId(Long userId, Long roleId);

    /**
     * Insert user-role mapping
     * @param userId the user ID
     * @param roleId the role ID
     * @return Mono of Void
     */
    @Modifying
    @Query("INSERT INTO user_roles (user_id, role_id) VALUES (:userId, :roleId)")
    Mono<Void> insertUserRole(Long userId, Long roleId);
}
