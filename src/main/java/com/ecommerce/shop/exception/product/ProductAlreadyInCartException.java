package com.ecommerce.shop.exception.product;

public class ProductAlreadyInCartException extends RuntimeException{
    public ProductAlreadyInCartException() {
        super("Product already in cart");
    }
}
