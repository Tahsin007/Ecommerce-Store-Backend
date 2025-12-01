package com.example.ecommerce.dto.response;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;
}

