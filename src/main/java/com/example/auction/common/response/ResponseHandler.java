package com.example.auction.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

    public static <T> ResponseEntity<ResponseWrapper<T>> success(T data, String message, HttpStatus status) {
        ResponseWrapper<T> response = new ResponseWrapper<>(true, message, data);
        return new ResponseEntity<>(response, status);
    }

    public static <T> ResponseEntity<ResponseWrapper<T>> error(String message, HttpStatus status) {
        ResponseWrapper<T> response = new ResponseWrapper<>(false, message, null);
        return new ResponseEntity<>(response, status);
    }
}
