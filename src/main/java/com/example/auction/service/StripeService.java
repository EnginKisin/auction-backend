package com.example.auction.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

@Service
public class StripeService {
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public String createStripeCustomer(String email, String cardToken) throws StripeException {
        com.stripe.Stripe.apiKey = stripeApiKey;

        Customer customer = Customer.create(
                Map.of(
                        "email", email,
                        "source", cardToken
                )
        );
        return customer.getId();
    }
}
