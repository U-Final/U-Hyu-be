package com.ureca.uhyu.global.config;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PermitAllURI {
    OAUTH2("/oauth2"),
    LOGIN("/login"),
    //MAP("/map"),
    BRAND_LIST("/brand-list"),
    HEALTH("/actuator/health");

    private final String uri;

    PermitAllURI(String uri) {
        this.uri = uri;
    }

    public static boolean isPermit(String requestUri) {
        return Arrays.stream(values())
                .map(PermitAllURI::getUri)
                .anyMatch(requestUri::startsWith);
    }

    /**
     * 모든 허용 Uri 확인용 메소드
     *
     * public static List<String> getAllUris() {
     *         return Arrays.stream(values())
     *                 .map(PermitAllURI::getUri)
     *                 .toList();
     *     }
     */
}