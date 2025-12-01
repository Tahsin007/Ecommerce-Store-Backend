package com.example.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

    private String title;

    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private Double price;

    private String description;

    private String category;

    private String image;
}
