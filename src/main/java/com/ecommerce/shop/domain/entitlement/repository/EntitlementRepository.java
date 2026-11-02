package com.ecommerce.shop.domain.entitlement.repository;

import com.ecommerce.shop.domain.entitlement.entity.EntitlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntitlementRepository extends JpaRepository<EntitlementEntity, Long> {
}
