package com.ecommerce.shop.domain.cart.controller;

import com.ecommerce.shop.domain.user.dto.JwtUserSubject;
import com.ecommerce.shop.domain.cart.dto.AddProductToCartRequest;
import com.ecommerce.shop.domain.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Void> addProductToCart(
            @RequestBody AddProductToCartRequest requestBody,
            @AuthenticationPrincipal JwtUserSubject currentUser
    ){
        cartService.addProductToCart(currentUser.id(), requestBody.productId());
        return ResponseEntity.status(200).build() ;

    }
}
