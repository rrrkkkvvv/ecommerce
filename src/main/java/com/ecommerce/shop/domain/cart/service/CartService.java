package com.ecommerce.shop.domain.cart.service;

import com.ecommerce.shop.exception.product.ProductAlreadyInCartException;
import com.ecommerce.shop.exception.product.ProductNotFoundException;
import com.ecommerce.shop.exception.user.UserNotFoundException;
import com.ecommerce.shop.domain.cart.entity.CartEntity;
import com.ecommerce.shop.domain.product.entity.ProductEntity;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import com.ecommerce.shop.domain.cart.repository.CartRepository;
import com.ecommerce.shop.domain.product.repository.ProductRepository;
import com.ecommerce.shop.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }
    @Transactional
    public void addProductToCart(Long userId, Long productId){
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        CartEntity cart = user.getCart();
        ProductEntity product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        if (cart.getProducts().stream()
                .anyMatch(p -> p.getId().equals(productId))) {
            throw new ProductAlreadyInCartException();
        }

        cart.getProducts().add(product);
    }

    public Set<ProductEntity> getProductsFromCart(Long userId){
        UserEntity user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return user.getCart().getProducts();
    }

}
