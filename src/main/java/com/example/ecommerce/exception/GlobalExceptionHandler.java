package com.example.ecommerce.exception;
import com.example.ecommerce.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResourceNotFoundException(
        ResourceNotFoundException ex, ServerWebExchange exchange) {
        log.error("Resource not found: {}", ex.getMessage());
        ErrorResponse error = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequestException(
        BadRequestException ex, ServerWebExchange exchange) {
        log.error("Bad request: {}", ex.getMessage());
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnauthorizedException(
        UnauthorizedException ex, ServerWebExchange exchange) {
        log.error("Unauthorized: {}", ex.getMessage());
        ErrorResponse error = buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error));
    }

//    @ExceptionHandler(ForbiddenException.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleForbiddenException(
//        ForbiddenException ex, ServerWebExchange exchange) {
//        log.error("Forbidden: {}", ex.getMessage());
//        ErrorResponse error = buildErrorResponse(
//            HttpStatus.FORBIDDEN,
//            ex.getMessage(),
//            exchange.getRequest().getPath().value()
//        );
//        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(error));
//    }

    @ExceptionHandler(TokenExpiredException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleTokenExpiredException(
        TokenExpiredException ex, ServerWebExchange exchange) {
        log.error("Token expired: {}", ex.getMessage());
        ErrorResponse error = buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error));
    }

//    @ExceptionHandler(InvalidTokenException.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleInvalidTokenException(
//        InvalidTokenException ex, ServerWebExchange exchange) {
//        log.error("Invalid token: {}", ex.getMessage());
//        ErrorResponse error = buildErrorResponse(
//            HttpStatus.BAD_REQUEST,
//            ex.getMessage(),
//            exchange.getRequest().getPath().value()
//        );
//        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
//    }

//    @ExceptionHandler(DuplicateResourceException.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleDuplicateResourceException(
//        DuplicateResourceException ex, ServerWebExchange exchange) {
//        log.error("Duplicate resource: {}", ex.getMessage());
//        ErrorResponse error = buildErrorResponse(
//            HttpStatus.CONFLICT,
//            ex.getMessage(),
//            exchange.getRequest().getPath().value()
//        );
//        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(error));
//    }

//    @ExceptionHandler(AccountDisabledException.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleAccountDisabledException(
//        AccountDisabledException ex, ServerWebExchange exchange) {
//        log.error("Account disabled: {}", ex.getMessage());
//        ErrorResponse error = buildErrorResponse(
//            HttpStatus.FORBIDDEN,
//            ex.getMessage(),
//            exchange.getRequest().getPath().value()
//        );
//        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(error));
//    }

//    @ExceptionHandler(EmailNotVerifiedException.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleEmailNotVerifiedException(
//        EmailNotVerifiedException ex, ServerWebExchange exchange) {
//        log.error("Email not verified: {}", ex.getMessage());
//        ErrorResponse error = buildErrorResponse(
//            HttpStatus.FORBIDDEN,
//            ex.getMessage(),
//            exchange.getRequest().getPath().value()
//        );
//        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(error));
//    }

//    @ExceptionHandler(RateLimitExceededException.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleRateLimitExceededException(
//        RateLimitExceededException ex, ServerWebExchange exchange) {
//        log.warn("Rate limit exceeded: {}", ex.getMessage());
//        ErrorResponse error = buildErrorResponse(
//            HttpStatus.TOO_MANY_REQUESTS,
//            ex.getMessage(),
//            exchange.getRequest().getPath().value()
//        );
//        return Mono.just(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error));
//    }

//    @ExceptionHandler(EmailSendException.class)
//    public Mono<ResponseEntity<ErrorResponse>> handleEmailSendException(
//        EmailSendException ex, ServerWebExchange exchange) {
//        log.error("Email send error: {}", ex.getMessage(), ex);
//        ErrorResponse error = buildErrorResponse(
//            HttpStatus.INTERNAL_SERVER_ERROR,
//            "Failed to send email. Please try again later.",
//            exchange.getRequest().getPath().value()
//        );
//        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
//    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleServiceUnavailableException(
        ServiceUnavailableException ex, ServerWebExchange exchange) {
        log.error("Service unavailable: {}", ex.getMessage());
        ErrorResponse error = buildErrorResponse(
            HttpStatus.SERVICE_UNAVAILABLE,
            ex.getMessage(),
            exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccessDeniedException(
        AccessDeniedException ex, ServerWebExchange exchange) {
        log.error("Access denied: {}", ex.getMessage());
        ErrorResponse error = buildErrorResponse(
            HttpStatus.FORBIDDEN,
            "Access denied. You don't have permission to access this resource.",
            exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(error));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadCredentialsException(
        BadCredentialsException ex, ServerWebExchange exchange) {
        log.error("Bad credentials: {}", ex.getMessage());
        ErrorResponse error = buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Invalid username or password",
            exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(
        WebExchangeBindException ex, ServerWebExchange exchange) {
        log.error("Validation error: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Validation failed");
        response.put("errors", errors);
        response.put("timestamp", LocalDateTime.now());
        response.put("path", exchange.getRequest().getPath().value());

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    @ExceptionHandler(WebClientResponseException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleWebClientResponseException(
        WebClientResponseException ex, ServerWebExchange exchange) {
        log.error("External API error: {}", ex.getMessage());
        ErrorResponse error = buildErrorResponse(
            HttpStatus.BAD_GATEWAY,
            "Error communicating with external service",
            exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGlobalException(
        Exception ex, ServerWebExchange exchange) {
        log.error("Unexpected error: ", ex);
        ErrorResponse error = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please try again later.",
            exchange.getRequest().getPath().value()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
            .status(status.value())
            .message(message)
            .timestamp(LocalDateTime.now())
            .path(path)
            .build();
    }
}
