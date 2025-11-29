package com.example.ecommerce.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("verification_tokens")
public class VerificationToken {
    @Id
    private Long id;
    private String token;
    private Long userId;
    private String tokenType;
    private LocalDateTime expiryDate;
    private LocalDateTime createdAt;
}
