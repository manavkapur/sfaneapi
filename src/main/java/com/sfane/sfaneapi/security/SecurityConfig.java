package com.sfane.sfaneapi.security;

import com.sfane.sfaneapi.service.AdminUserService;
import com.sfane.sfaneapi.service.BlacklistedTokenService;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final AdminUserService adminUserService;
    private final BlacklistedTokenService blacklistedTokenService;

    public SecurityConfig(JwtUtil jwtUtil, AdminUserService adminUserService, BlacklistedTokenService blacklistedTokenService) {
        this.jwtUtil = jwtUtil;
        this.adminUserService = adminUserService;
        this.blacklistedTokenService =blacklistedTokenService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, adminUserService, blacklistedTokenService);
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/phone-login").permitAll()
                        .requestMatchers("/cart/**", "/orders/**").hasRole("USER")
                        .anyRequest().permitAll()
                )

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
