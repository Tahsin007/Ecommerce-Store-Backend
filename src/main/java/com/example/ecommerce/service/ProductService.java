package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.request.UpdateProductRequest;
import com.example.ecommerce.dto.response.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final WebClient webClient;

    @Value("${fakestore.api.base-url}")
    private String fakeStoreBaseUrl;

    public Flux<ProductDTO> getAllProducts() {
        return webClient.get()
            .uri(fakeStoreBaseUrl + "/products")
            .retrieve()
            .bodyToFlux(ProductDTO.class)
            .collectList()
            .flatMapMany(products -> Flux.fromIterable(new ArrayList<>(products)))
            .doOnError(e -> log.error("Error fetching products", e));
    }

    public Mono<Object> addProduct(CreateProductRequest request) {
        Map<String, Object> productData = new HashMap<>();
        productData.put("title", request.getTitle());
        productData.put("price", request.getPrice());
        productData.put("description", request.getDescription());
        productData.put("category", request.getCategory());
        productData.put("image", request.getImage());

        return webClient.post()
            .uri(fakeStoreBaseUrl + "/products")
            .bodyValue(productData)
            .retrieve()
            .bodyToMono(Object.class)
            .doOnSuccess(product -> log.info("Product created successfully"))
            .doOnError(e -> log.error("Error creating product", e));
    }

    public Mono<Object> updateProduct(Long id, UpdateProductRequest request) {
        Map<String, Object> productData = new HashMap<>();

        if (request.getTitle() != null) {
            productData.put("title", request.getTitle());
        }
        if (request.getPrice() != null) {
            productData.put("price", request.getPrice());
        }
        if (request.getDescription() != null) {
            productData.put("description", request.getDescription());
        }
        if (request.getCategory() != null) {
            productData.put("category", request.getCategory());
        }
        if (request.getImage() != null) {
            productData.put("image", request.getImage());
        }

        return webClient.put()
            .uri(fakeStoreBaseUrl + "/products/" + id)
            .bodyValue(productData)
            .retrieve()
            .bodyToMono(Object.class)
            .doOnSuccess(product -> log.info("Product updated successfully: {}", id))
            .doOnError(e -> log.error("Error updating product: {}", id, e));
    }

    public Mono<Object> deleteProduct(Long id) {
        return webClient.delete()
            .uri(fakeStoreBaseUrl + "/products/" + id)
            .retrieve()
            .bodyToMono(Object.class)
            .doOnSuccess(product -> log.info("Product deleted successfully: {}", id))
            .doOnError(e -> log.error("Error deleting product: {}", id, e));
    }

    public Mono<ProductDTO> getProductById(Long id) {
        return webClient.get()
            .uri(fakeStoreBaseUrl + "/products/" + id)
            .retrieve()
            .bodyToMono(ProductDTO.class)
            .doOnError(e -> log.error("Error fetching product by id: {}", id, e));
    }

    public Flux<ProductDTO> getProductsByCategory(String category) {
        return webClient.get()
            .uri(fakeStoreBaseUrl + "/products/category/" + category)
            .retrieve()
            .bodyToFlux(ProductDTO.class)
            .collectList()
            .flatMapMany(products -> Flux.fromIterable(new ArrayList<>(products)))
            .doOnError(e -> log.error("Error fetching products by category: {}", category, e));
    }

    public Flux<String> getAllCategories() {
        return webClient.get()
            .uri(fakeStoreBaseUrl + "/products/categories")
            .retrieve()
            .bodyToFlux(String.class)
            .collectList()
            .flatMapMany(categories -> Flux.fromIterable(new ArrayList<>(categories)))
            .doOnError(e -> log.error("Error fetching categories", e));
    }
}
