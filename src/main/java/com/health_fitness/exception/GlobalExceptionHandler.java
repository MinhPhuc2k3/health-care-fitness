package com.health_fitness.exception;

import com.health_fitness.model.ErrorDetail;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

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

}
