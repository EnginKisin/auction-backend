package com.example.auction.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.auction.common.response.ResponseHandler;
import com.example.auction.common.response.ResponseWrapper;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleNotFound(NotFoundException ex) {
        return ResponseHandler.error(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseHandler.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseHandler.error(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StripeProcessException.class)
    public ResponseEntity<ResponseWrapper<Object>> handleStripeWrapped(StripeProcessException ex) {
        return ResponseHandler.error(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Object>> handleOtherErrors(Exception ex) {
        return ResponseHandler.error("Sunucu hatası: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
