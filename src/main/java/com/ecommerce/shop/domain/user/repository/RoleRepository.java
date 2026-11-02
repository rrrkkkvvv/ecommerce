package com.ecommerce.shop.domain.user.repository;

import com.ecommerce.shop.domain.user.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleRepository extends JpaRepository<RoleEntity, Long> {


    RoleEntity findByName(String name);
}
