package com.health_fitness.exception;

import com.health_fitness.model.ErrorDetail;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorDetail> handleAuthException(AuthException exception){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetail);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDetail> handleAuthException(AuthenticationException exception){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
    }

    @ExceptionHandler(UpdateMealBeforeCurrentDateException.class)
    public ResponseEntity<ErrorDetail> handleUpdateMealBeforeCurrentDateException(UpdateMealBeforeCurrentDateException exception){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDetail> handleBadRequestException(BadRequestException exception){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetail);
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDetail> handleBadRequestException(NotFoundException exception){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetail);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorDetail> handleExpiredJwtException(ExpiredJwtException exception){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setDetail(exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    Map<String, String> e = new HashMap<>();
                    e.put("field", error.getField());
                    e.put("message", error.getDefaultMessage());
                    return e;
                })
                .toList();

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Validation failed");
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


}
