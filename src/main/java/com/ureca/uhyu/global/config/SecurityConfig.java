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
                                "/", "/login", "/oauth2/**",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/brand-list/**",
                                "/map/stores", "detail/map/**"
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
                            log.warn("Access Denied: {}", accessDeniedException.getMessage());
                            response.sendRedirect("/user/extra-info"); // AccessDenied 발생 시 리다이렉트
                        })
                )
                // 이 필터는 ROLE_TMP_USER 처리를 위한 것이므로, 기존 위치 유지
                .addFilterAfter(new OncePerRequestFilter() {
                    @Override
                    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                            throws ServletException, IOException {

                        log.info("ROLE_TMP_USER 확인하는 필터로 들어옴!!");

                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                        if (authentication != null && authentication.isAuthenticated()) {
                            log.info("현재 인증된 사용자: {}", authentication.getName());
                            log.info("권한 목록: {}", authentication.getAuthorities());

                            boolean isTmpUser = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_TMP_USER"));

                            if (isTmpUser) {
                                String uri = request.getRequestURI();
                                log.info("✅ ROLE_TMP_USER 감지됨. 요청 URI: {}", uri);

                                if (!uri.equals("/user/extra-info")
                                        && !uri.startsWith("/static/")
                                        && !uri.equals("/user/check-email")) {

                                    log.info("👉 TMP_USER가 /user/extra-info로 리다이렉트: {}", uri);
                                    response.sendRedirect("/user/extra-info");
                                    return;
                                }
                            } else {
                                log.info("⛳ ROLE_TMP_USER가 아님 → 정상 사용자, 다음 필터 진행");
                            }
                        }

                        log.info("또 다음 필터로 넘어감! ROLE_TMP_USER 확인은 끝남. ");
                        filterChain.doFilter(request, response);
                    }
                }, UsernamePasswordAuthenticationFilter.class); // UsernamePasswordAuthenticationFilter 뒤에

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173",
                "https://www.u-hyu.site",
                "https://api.u-hyu.site"
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