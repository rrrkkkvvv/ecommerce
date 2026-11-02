package com.ecommerce.shop.domain.order.controller;

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

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/payment")
    public ResponseEntity<OrderPaymentInitResponseDto> initiateOrderPayment(
            @AuthenticationPrincipal JwtUserSubject currentUser
    ){
        OrderPaymentInitResponseDto response = orderService.initiateOrderPayment(currentUser.id());
        return ResponseEntity.status(200).body(response);

    }

}
