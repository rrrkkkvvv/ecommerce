package com.ecommerce.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.AuthorizationManagerFactory;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private final JwtService jwtService;

    @Autowired
    public SecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtService);
    }
    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_USER"
        );

    }
    @Bean
    public AuthorizationManagerFactory<RequestAuthorizationContext> authorizationManagerFactory(
            RoleHierarchy roleHierarchy
    ) {
        DefaultAuthorizationManagerFactory<RequestAuthorizationContext> factory =
                new DefaultAuthorizationManagerFactory<>();

        factory.setRoleHierarchy(roleHierarchy);

        return factory;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/auth/refresh").authenticated()
                                .requestMatchers("/testAdmin/**").hasRole("ADMIN")
                                .requestMatchers("/testUser/**").hasRole("USER")
                                .requestMatchers("/cart/**").hasRole("USER")
                                .requestMatchers("/order/**").hasRole("USER")

                                .requestMatchers("/webhook/**").permitAll()

                                .requestMatchers("/auth/**").permitAll()
                                .anyRequest().authenticated()


                )
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        ;

        return http.build();
    }
}
