package com.ureca.uhyu.global.config;

import com.ureca.uhyu.domain.auth.handler.OAuth2SuccessHandler;
import com.ureca.uhyu.domain.auth.jwt.JwtAuthenticationFilter;
import com.ureca.uhyu.domain.auth.jwt.JwtTokenProvider;
import com.ureca.uhyu.domain.auth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.ureca.uhyu.domain.auth.repository.TokenRepository;
import com.ureca.uhyu.domain.auth.service.CustomUserDetailsService;
import com.ureca.uhyu.domain.auth.service.TokenService;
import com.ureca.uhyu.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenService, userRepository);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(FrameOptionsConfig::disable))
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/oauth2/**"),
                                new AntPathRequestMatcher("/map/stores"),
                                new AntPathRequestMatcher("/brand-list/**"),
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/v3/api-docs/**"),
                                new AntPathRequestMatcher("/actuator/health")
                        ).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestRepository(authorizationRequestRepository())
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler())
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, tokenRepository, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/user/extra-info");
                        })
                )
                .addFilterAfter(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                            throws ServletException, IOException {
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (authentication != null && authentication.isAuthenticated() &&
                                authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ROLE_TMP_USER"))) {
                            String uri = request.getRequestURI();
                            if (!uri.equals("/user/extra-info")
                                    && !uri.startsWith("/static/")
                                    && !uri.equals("/user/check-email")) {
                                response.sendRedirect("/user/extra-info");
                                return;
                            }
                        }
                        filterChain.doFilter(request, response);
                    }
                }, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://www.u-hyu.site"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // 캐시 시간 (1시간)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
