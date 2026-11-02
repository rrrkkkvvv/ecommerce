package com.ecommerce.shop.domain.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Table(name = "digital_asset")
@Entity
public class DigitalAssetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String fileName;

    @NotNull
    private String storageKey;

    @NotNull
    private String contentType;

    @NotNull
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;


}
