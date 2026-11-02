package com.ecommerce.shop.exception.user;

public class UserAccessDeniedException extends RuntimeException {
    public UserAccessDeniedException() {
        super("Denied access");
    }

}
