package com.seera.lumi.partner.service.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        BaseError error = ex.getError();
        log.warn("Business error: code={}, message={}", error.getCode(), error.getDesc());
        return ResponseEntity.status(error.getHttpStatus())
                .body(error.toErrorResponse());
    }

    @ExceptionHandler(PartnerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePartnerNotFound(PartnerNotFoundException ex) {
        log.warn("Partner not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .code("PARTNER-2001")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(PartnerAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePartnerAlreadyExists(PartnerAlreadyExistsException ex) {
        log.warn("Partner already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .code("PARTNER-2003")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.builder()
                        .code("PARTNER-1003")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(PartnerSuspendedException.class)
    public ResponseEntity<ErrorResponse> handlePartnerSuspended(PartnerSuspendedException ex) {
        log.warn("Partner suspended: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder()
                        .code("PARTNER-2002")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .code("PARTNER-1007")
                        .message("Validation failed: " + errors)
                        .build());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException ex) {
        log.error("Downstream service error: status={}, message={}", ex.status(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.builder()
                        .code("PARTNER-5001")
                        .message("Downstream service error")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .code("PARTNER-1001")
                        .message("Internal server error")
                        .build());
    }
}
