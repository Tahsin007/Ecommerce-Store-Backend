package com.example.ecommerce.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final WebClient webClient;

    @Value("${fakestore.api.base-url}")
    private String fakeStoreBaseUrl;

    public Flux<Object> getAllProducts() {
        return webClient.get()
            .uri(fakeStoreBaseUrl + "/products")
            .retrieve()
            .bodyToFlux(Object.class)
            .doOnError(e -> log.error("Error fetching products", e));
    }

    public Mono<Object> getProductById(Long id) {
        return webClient.get()
            .uri(fakeStoreBaseUrl + "/products/" + id)
            .retrieve()
            .bodyToMono(Object.class)
            .doOnError(e -> log.error("Error fetching product by id: {}", id, e));
    }

    public Flux<Object> getProductsByCategory(String category) {
        return webClient.get()
            .uri(fakeStoreBaseUrl + "/products/category/" + category)
            .retrieve()
            .bodyToFlux(Object.class)
            .doOnError(e -> log.error("Error fetching products by category: {}", category, e));
    }

    public Flux<String> getAllCategories() {
        return webClient.get()
            .uri(fakeStoreBaseUrl + "/products/categories")
            .retrieve()
            .bodyToFlux(String.class)
            .doOnError(e -> log.error("Error fetching categories", e));
    }
}
