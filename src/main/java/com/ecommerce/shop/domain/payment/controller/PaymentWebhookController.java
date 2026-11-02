package com.ecommerce.shop.domain.payment.controller;

import com.ecommerce.shop.domain.order.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class PaymentWebhookController {
    @Value("${stripe.webhook-secret-key}")
    private String endpointSecret;
    private final OrderService orderService;
    public PaymentWebhookController(OrderService orderService){
        this.orderService= orderService;
    }

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            orderService.capturePayment(event);
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid webhook signature", e);
        }
    }

}
