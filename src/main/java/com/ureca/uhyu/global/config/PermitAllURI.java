package com.ureca.uhyu.global.config;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PermitAllURI {
    OAUTH2("/oauth2"),
    LOGIN("/login"),
    MAP_STORES("/map/stores"),
    SWAGGER("/swagger-ui"),
    DOCS("/v3/api-docs"),
    ROOT("/"),
    MAP("/map"),
    BRAND_LIST("/brand-list"),
    HEALTH("/actuator/health");

    private final String uri;

    PermitAllURI(String uri) {
        this.uri = uri;
    }

    public static boolean isPermit(String requestUri) {
        return Arrays.stream(values())
                .map(PermitAllURI::getUri)
                .anyMatch(requestUri::startsWith); // equals로 변경함.(세분화되게 위에 추가될 예정)
    }
}