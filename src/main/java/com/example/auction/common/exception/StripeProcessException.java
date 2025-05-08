package com.example.auction.common.exception;

public class StripeProcessException extends RuntimeException {
    public StripeProcessException(String message) {
        super(message);
    }
}
