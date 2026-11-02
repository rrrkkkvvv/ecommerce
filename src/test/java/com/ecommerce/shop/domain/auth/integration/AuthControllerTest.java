package com.ecommerce.shop.domain.auth.integration;

import com.ecommerce.shop.domain.auth.dto.SignUpRequest;
import com.ecommerce.shop.domain.cart.entity.CartEntity;
import com.ecommerce.shop.domain.cart.repository.CartRepository;
import com.ecommerce.shop.domain.user.entity.RoleEntity;
import com.ecommerce.shop.domain.user.entity.Roles;
import com.ecommerce.shop.domain.user.entity.UserEntity;
import com.ecommerce.shop.domain.user.repository.RoleRepository;
import com.ecommerce.shop.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @MockitoBean
    UserRepository userRepository;
    @MockitoBean
    RoleRepository roleRepository;
    @MockitoBean
    CartRepository cartRepository;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void testSignUp() throws Exception {


        String password = "password";

        SignUpRequest signUpRequest = new SignUpRequest("email@email.com", "testName", password);
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(signUpRequest.email());
        userEntity.setName(signUpRequest.name());
        RoleEntity role = new RoleEntity();
        role.setName(String.valueOf(Roles.ROLE_USER));
        userEntity.setRoles(List.of(role));

        when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.empty());

        when(roleRepository.findByName(String.valueOf(Roles.ROLE_USER))).thenReturn(role);
        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

        String json = ow.writeValueAsString(signUpRequest);

        mockMvc.perform( post("/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )

                .andExpect(status().is(201))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(userRepository).findByEmail(userEntity.getEmail());
        verify(roleRepository, times(2)).findByName(String.valueOf(Roles.ROLE_USER));


        verify(userRepository, times(2)).save(any(UserEntity.class));
        verify(cartRepository).save(any(CartEntity.class));


    }
}
