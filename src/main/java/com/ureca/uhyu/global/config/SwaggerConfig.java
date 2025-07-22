package com.ureca.uhyu.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("개발 서버"),
                        new Server().url("http://13.209.87.43:8080").description("운영 서버")
                ))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("JWT 토큰을 입력하세요. 'Bearer ' 접두사는 자동으로 추가됩니다.")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

    private Info apiInfo() {
        return new Info()
                .title("U-HYU API")
                .description("""
                        ## U-HYU API 명세서
                        
                        ### 인증 방법
                        1. OAuth2를 통해 카카오 로그인
                        2. 발급받은 JWT 토큰을 Authorization 헤더에 포함
                        3. 형식: `Bearer {token}`
                        
                        ### 사용자 권한
                        - **TMP_USER**: 임시 사용자 (온보딩 정보 입력 필요)
                        - **USER**: 일반 사용자
                        - **ADMIN**: 관리자
                        
                        ### 공통 응답 형식
                        모든 API는 CommonResponse 형태로 응답합니다.
                        ```json
                        {
                          "statusCode": 0,
                          "message": "정상 처리 되었습니다.",
                          "data": { ... }
                        }
                        ```
                        """)
                .version("v1.0.0")
                .contact(new Contact()
                        .name("U-HYU Team")
                        .email("support@uhyu.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/auth/**", "/oauth2/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .pathsToMatch("/user/**", "/users/**")
                .build();
    }

    @Bean
    public GroupedOpenApi mapApi() {
        return GroupedOpenApi.builder()
                .group("map")
                .pathsToMatch("/map/**")
                .build();
    }

    @Bean
    public GroupedOpenApi brandApi() {
        return GroupedOpenApi.builder()
                .group("brand")
                .pathsToMatch("/brand-list/**")
                .build();
    }

    @Bean
    public GroupedOpenApi myMapApi() {
        return GroupedOpenApi.builder()
                .group("mymap")
                .pathsToMatch("/mymap/**")
                .build();
    }

    @Bean
    public GroupedOpenApi allApis() {
        return GroupedOpenApi.builder()
                .group("all")
                .pathsToMatch("/**")
                .build();
    }
}

