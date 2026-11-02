package com.ecommerce.shop.domain.product.repository;

import com.ecommerce.shop.domain.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
