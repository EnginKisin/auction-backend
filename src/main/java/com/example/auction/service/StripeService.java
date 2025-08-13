package com.example.auction.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;

@Service
public class StripeService {
    // @Value("${stripe.api.key}")
    // private String stripeApiKey;

    // public String createStripeCustomer(String email, String cardToken) throws StripeException {
    //     com.stripe.Stripe.apiKey = stripeApiKey;

    //     Customer customer = Customer.create(
    //             Map.of(
    //                     "email", email,
    //                     "source", cardToken
    //             )
    //     );
    //     return customer.getId();
    // }

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public String createStripeCustomer(String email) throws StripeException {
        com.stripe.Stripe.apiKey = stripeApiKey;
        Customer customer = Customer.create(Map.of("email", email));
        return customer.getId();
    }

    public void attachPaymentMethodToCustomer(String customerId, String paymentMethodId) throws StripeException {
        com.stripe.Stripe.apiKey = stripeApiKey;

        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.attach(Map.of("customer", customerId));

        // Mevcut müşteriyi çekip güncelle
        Customer customer = Customer.retrieve(customerId);
        customer.update(Map.of(
            "invoice_settings", Map.of("default_payment_method", paymentMethodId)
        ));
    }

    public String chargeCustomerWithPaymentIntent(String customerId, String paymentMethodId, Double amount, String currency) throws StripeException {
        com.stripe.Stripe.apiKey = stripeApiKey;

        PaymentIntent paymentIntent = PaymentIntent.create(Map.of(
            "amount", (long)(amount * 100),
            "currency", currency,
            "customer", customerId,
            "payment_method", paymentMethodId,
            "off_session", true, // müşteri çevrimdışı
            "confirm", true
        ));

        return paymentIntent.getId();
    }
}
