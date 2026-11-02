package com.ecommerce.shop.exception.payment;

public class PaymentNotFoundException extends RuntimeException{
    public PaymentNotFoundException() {
        super("Payment not found");
    }
}
