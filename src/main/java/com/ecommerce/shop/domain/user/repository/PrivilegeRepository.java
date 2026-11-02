package com.ecommerce.shop.domain.user.repository;

import com.ecommerce.shop.domain.user.entity.PrivilegeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<PrivilegeEntity, Long> {
    PrivilegeEntity findByName(String name);
}