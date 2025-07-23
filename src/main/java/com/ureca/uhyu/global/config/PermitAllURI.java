package com.ureca.uhyu.global.config;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PermitAllURI {
    OAUTH2("/oauth2/authorization/kakao"),
    LOGIN("/login/oauth2/code/kakao"),
    MAP_STORES("/map/stores"),
    SWAGGER("/swagger-ui"),
    DOCS("/v3/api-docs"),
    ROOT("/"),
    BRAND_LIST("/brand-list");

    private final String uri;

    PermitAllURI(String uri) {
        this.uri = uri;
    }

    public static boolean isPermit(String requestUri) {
        return Arrays.stream(values())
                .map(PermitAllURI::getUri)
                .anyMatch(requestUri::equals);
    }
}