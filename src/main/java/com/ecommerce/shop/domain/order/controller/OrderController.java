package com.ecommerce.shop.domain.order.controller;

import com.ecommerce.shop.domain.order.dto.Order;
import com.ecommerce.shop.domain.order.dto.OrderPaymentInitResponseDto;
import com.ecommerce.shop.domain.user.dto.JwtUserSubject;
import com.ecommerce.shop.domain.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
//    CREATE ORDER OPERATION
//    GET LIST OF ORDERS OPERATION
//    PAY FOR ORDER OPERATION -> get/create CheckoutAttempt, PaymentEntity for it, then generate url and return it on the frontend
//    User finishes payment on StripeSession -> webhook capture -> CheckoutAttempt/Order/Payment status change COMPLETED, succeeded
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
//    TODO:REPLACE ON CheckoutAttempt payment
//    @PostMapping("/payment")
//    public ResponseEntity<OrderPaymentInitResponseDto> initiateOrderPayment(
//            @AuthenticationPrincipal JwtUserSubject currentUser
//    ){
//        OrderPaymentInitResponseDto response = orderService.initiateOrderPayment(currentUser.id());
//        return ResponseEntity.status(200).body(response);
//    }
    @PostMapping("/createOrder")
    public ResponseEntity<Order> createOrder(@AuthenticationPrincipal JwtUserSubject currentUser){
        Order response = orderService.createOrder(currentUser.id());
        return ResponseEntity.status(200).body(response);
    }
}
