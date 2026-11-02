package com.ecommerce.shop.domain.auth.controller;

import com.ecommerce.shop.domain.auth.dto.AuthResponse;
import com.ecommerce.shop.domain.auth.dto.RefreshAuthRequest;
import com.ecommerce.shop.domain.auth.dto.SignInRequest;
import com.ecommerce.shop.domain.auth.dto.SignUpRequest;
import com.ecommerce.shop.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signUp")
    public ResponseEntity<AuthResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest){
        log.info("User with email: '"+signUpRequest.name()+"' was signed up");
        return ResponseEntity.status(201).body(authService.signUp(signUpRequest)) ;
    }
    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> signIn(@RequestBody @Valid SignInRequest signInRequest){
        log.info("Sign in by: '"+signInRequest.email());
        return ResponseEntity.status(200).body(authService.signIn(signInRequest)) ;
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshAuthRequest refreshAuthRequest)  {
        log.info("Refresh of user by"+refreshAuthRequest.token()+"' jwt");
        return ResponseEntity.status(200).body(authService.refresh(refreshAuthRequest)) ;
    }
}
