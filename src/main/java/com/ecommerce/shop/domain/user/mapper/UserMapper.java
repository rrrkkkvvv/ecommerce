package com.ecommerce.shop.domain.user.mapper;

import com.ecommerce.shop.domain.user.dto.User;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User convertToDomain(UserEntity userEntity){
        return new User(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getName()
        );
    }
}
