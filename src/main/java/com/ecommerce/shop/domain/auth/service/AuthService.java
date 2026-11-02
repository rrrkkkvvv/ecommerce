package com.ecommerce.shop.domain.auth.service;

import com.ecommerce.shop.exception.user.InvalidCredentialsException;
import com.ecommerce.shop.domain.user.mapper.RolesMapper;
import com.ecommerce.shop.domain.user.mapper.UserMapper;
import com.ecommerce.shop.domain.auth.dto.AuthResponse;
import com.ecommerce.shop.domain.auth.dto.RefreshAuthRequest;
import com.ecommerce.shop.domain.auth.dto.SignInRequest;
import com.ecommerce.shop.domain.auth.dto.SignUpRequest;
import com.ecommerce.shop.domain.user.dto.JwtUserSubject;
import com.ecommerce.shop.domain.cart.entity.CartEntity;
import com.ecommerce.shop.domain.user.entity.RoleEntity;
import com.ecommerce.shop.domain.user.entity.Roles;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import com.ecommerce.shop.domain.cart.repository.CartRepository;
import com.ecommerce.shop.domain.user.repository.RoleRepository;
import com.ecommerce.shop.domain.user.repository.UserRepository;
import com.ecommerce.shop.config.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RolesMapper rolesMapper;

    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;




    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder, UserMapper userMapper, RolesMapper rolesMapper, RoleRepository roleRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;

        this.userMapper = userMapper;
        this.rolesMapper = rolesMapper;
        this.roleRepository = roleRepository;
        this.cartRepository = cartRepository;
    }

    public AuthResponse signUp(SignUpRequest signUpRequest){
        if(userRepository.findByEmail(signUpRequest.email()).isPresent()){
            throw new InvalidCredentialsException();
        }
        String hashedPassword =  passwordEncoder.encode(signUpRequest.password());

        UserEntity userEntity = new UserEntity(signUpRequest.email(), signUpRequest.name(),hashedPassword);
        RoleEntity userRole = roleRepository.findByName(String.valueOf(Roles.ROLE_USER));

        userEntity.setRoles(List.of(userRole));
        UserEntity savedUserEntity =  userRepository.save(userEntity);
        CartEntity cartEntity = new CartEntity();

        cartEntity.setUser(userEntity);
        cartRepository.save(cartEntity);

        String token = jwtService.buildToken(new JwtUserSubject(savedUserEntity.getId(), savedUserEntity.getEmail(), Set.of(Roles.ROLE_USER)));


        return new AuthResponse(userMapper.convertToDomain(savedUserEntity), token);
    }
    public AuthResponse signIn(SignInRequest signInRequest){
        UserEntity userEntity = userRepository.findByEmail(signInRequest.email()).orElseThrow(InvalidCredentialsException::new);

        if(!passwordEncoder.matches(signInRequest.password(),userEntity.getPassword())){
            throw new InvalidCredentialsException();
        }

        String token = jwtService.buildToken(new JwtUserSubject(userEntity.getId(), userEntity.getEmail(), rolesMapper.convertToDomain(userEntity.getRoles()) ));
        return new AuthResponse(userMapper.convertToDomain(userEntity), token);
    }
    public AuthResponse refresh(RefreshAuthRequest refreshAuthRequest)  {

        JwtUserSubject userData = jwtService.extractUserData(refreshAuthRequest.token());

        UserEntity userEntity = userRepository.findById(userData.id()).orElseThrow(InvalidCredentialsException::new);

        String token = jwtService.buildToken(new JwtUserSubject(userEntity.getId(), userEntity.getEmail(), rolesMapper.convertToDomain(userEntity.getRoles())) );

        return new AuthResponse(userMapper.convertToDomain(userEntity), token);
    }

}
