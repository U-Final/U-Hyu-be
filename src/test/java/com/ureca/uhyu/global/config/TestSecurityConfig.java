package com.ureca.uhyu.global.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(Customizer.withDefaults())
                .logout(Customizer.withDefaults());

        return http.build();
    }

    /**
     * @Bean
     * public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
     *     return http
     *         .securityMatcher("/**")
     *         .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
     *         .build();
     * }
     */
}