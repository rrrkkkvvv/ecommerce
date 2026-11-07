package com.ecommerce.shop.exception.order;

public class OrderNotFoundException extends  RuntimeException{
    public OrderNotFoundException() {
        super("Order not found");
    }
}
