package com.ecommerce.shop.domain.user.mapper;

import com.ecommerce.shop.domain.user.entity.RoleEntity;
import com.ecommerce.shop.domain.user.entity.Roles;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RolesMapper {

    public Set<Roles> convertToDomain(List<RoleEntity> rolesEntity){

        return rolesEntity.stream().map( r-> Roles.valueOf(r.getName())).collect(Collectors.toSet());
    }
}
