package com.ecommerce.shop.domain.auth.unit;

import com.ecommerce.shop.config.JwtService;
import com.ecommerce.shop.domain.auth.dto.AuthResponse;
import com.ecommerce.shop.domain.auth.dto.RefreshAuthRequest;
import com.ecommerce.shop.domain.auth.dto.SignInRequest;
import com.ecommerce.shop.domain.auth.dto.SignUpRequest;
import com.ecommerce.shop.domain.auth.service.AuthService;
import com.ecommerce.shop.domain.cart.entity.CartEntity;
import com.ecommerce.shop.domain.cart.repository.CartRepository;
import com.ecommerce.shop.domain.user.dto.JwtUserSubject;
import com.ecommerce.shop.domain.user.dto.User;
import com.ecommerce.shop.domain.user.entity.RoleEntity;
import com.ecommerce.shop.domain.user.entity.Roles;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import com.ecommerce.shop.domain.user.mapper.RolesMapper;
import com.ecommerce.shop.domain.user.mapper.UserMapper;
import com.ecommerce.shop.domain.user.repository.RoleRepository;
import com.ecommerce.shop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith({MockitoExtension.class, })
public class AuthServiceTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CartRepository cartRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = new UserMapper();
    @Mock
    private  RolesMapper rolesMapper;

    private AuthService authService;

    @BeforeEach
    public void init(){
        this.authService = new AuthService(
                userRepository,
                jwtService,
                passwordEncoder,
                userMapper,
                rolesMapper,
                roleRepository,
                cartRepository
        );
    }

    @Test
    public void shouldSignUpUser(){
        String password = "password";
        String encodedPassword = "encodedPassword";

        String token = "token";
        SignUpRequest signUpRequest = new SignUpRequest("email@email.com", "testName", password);



        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(signUpRequest.email());
        userEntity.setPassword(encodedPassword);
        userEntity.setName(signUpRequest.name());

        User user =  userMapper.convertToDomain(userEntity);


        RoleEntity role = new RoleEntity();
        role.setName(String.valueOf(Roles.ROLE_USER));
        userEntity.setRoles(List.of(role));

        AuthResponse authResponse = new AuthResponse(user, token);
        when(userRepository.findByEmail(user.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signUpRequest.password())).thenReturn(encodedPassword);
        when(roleRepository.findByName(String.valueOf(Roles.ROLE_USER))).thenReturn(role);
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.buildToken(any(JwtUserSubject.class))).thenReturn(token);
        AuthResponse result = authService.signUp(signUpRequest);
        assertThat(authService).isNotNull();
        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        ArgumentCaptor<CartEntity> cartEntityCaptor = ArgumentCaptor.forClass(CartEntity.class);

        verify(userRepository).findByEmail(user.email());
        verify(passwordEncoder).encode(signUpRequest.password());
        verify(roleRepository).findByName(String.valueOf(Roles.ROLE_USER));
        verify(userRepository).save(any(UserEntity.class));
        verify(jwtService).buildToken(any(JwtUserSubject.class));


        verify(userRepository).save(userEntityCaptor.capture());
        verify(cartRepository).save(cartEntityCaptor.capture());

        UserEntity capturedUser = userEntityCaptor.getValue();
        assertThat(capturedUser.getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(capturedUser.getName()).isEqualTo(userEntity.getName());
        assertThat(capturedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(capturedUser.getRoles()).containsExactly(role);
        assertThat(result.token()).isEqualTo(token);



    }
    @Test
    public void shouldSignInUser(){
        String encodedPassword = "encodedPassword";
        String password = "password";
        String token = "token";

        SignInRequest signInRequest = new SignInRequest("email@email.com",  password);

        UserEntity userEntity = new UserEntity();

        userEntity.setId(1L);
        userEntity.setEmail(signInRequest.email());
        userEntity.setPassword(encodedPassword);
        userEntity.setName("name");

        when(userRepository.findByEmail(signInRequest.email())).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(signInRequest.password(), userEntity.getPassword())).thenReturn(true);
        when(jwtService.buildToken(any(JwtUserSubject.class))).thenReturn(token);
        AuthResponse authResponse = authService.signIn(signInRequest);

        verify(userRepository).findByEmail(signInRequest.email());
        verify(passwordEncoder).matches(
                signInRequest.password(),
                encodedPassword
        );
        verify(jwtService).buildToken(any(JwtUserSubject.class));

        assertThat(authResponse.user().email()).isEqualTo(userEntity.getEmail());
        assertThat(authResponse.token()).isEqualTo(token);

    }
    @Test
    public void shouldRefreshAuth(){

        String token = "token";

        RefreshAuthRequest refreshAuthRequest = new RefreshAuthRequest(token);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("email@email.com");
        userEntity.setName("name");

        RoleEntity role = new RoleEntity();
        role.setName(String.valueOf(Roles.ROLE_USER));

        List<RoleEntity> roles = List.of(role);
        JwtUserSubject jwtUserSubject = new JwtUserSubject(
                userEntity.getId(),
                userEntity.getEmail(),
                rolesMapper.convertToDomain(roles)
        );
        when(jwtService.extractUserData(refreshAuthRequest.token())).thenReturn(jwtUserSubject);
        when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));
        when(jwtService.buildToken(any(JwtUserSubject.class))).thenReturn(token);

        AuthResponse authResponse = authService.refresh(refreshAuthRequest);

        verify(jwtService).extractUserData(refreshAuthRequest.token());
        verify(userRepository).findById(userEntity.getId());
        verify(jwtService).buildToken(any(JwtUserSubject.class));

        assertThat(authResponse.user().email()).isEqualTo(userEntity.getEmail());
        assertThat(authResponse.token()).isEqualTo(token);


    }

}
