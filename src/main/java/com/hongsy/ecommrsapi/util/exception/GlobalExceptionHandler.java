package com.hongsy.ecommrsapi.util.exception;

import com.hongsy.ecommrsapi.util.common.CommonErrorResponse;
import com.hongsy.ecommrsapi.util.common.CommonResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CommonErrorResponse> handleCustomException(final CustomException e) {
        return ResponseEntity.status(e.getStatusCode()).body(
            CommonErrorResponse.builder()
                .message(e.getMessage())
                .error(e.getStatusCode().getReasonPhrase())
                .statusCode(e.getStatusCode().value())
                .timestamp(LocalDateTime.now())
                .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(ex.getStatusCode())
            .body(CommonErrorResponse.builder()
                .message(ex.getBindingResult().getFieldError().getDefaultMessage())
                .error(HttpStatus.valueOf(ex.getStatusCode().value()).getReasonPhrase())
                .statusCode(ex.getStatusCode().value())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CommonResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().iterator().next().getMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new CommonResponse<>(message, 400, null));
    }
}
