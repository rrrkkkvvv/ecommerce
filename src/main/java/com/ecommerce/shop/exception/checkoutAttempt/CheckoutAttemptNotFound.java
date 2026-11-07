package com.ecommerce.shop.exception.checkoutAttempt;

public class CheckoutAttemptNotFound extends RuntimeException {
    public CheckoutAttemptNotFound() {
        super("Checkout attempt not found");
    }
}
