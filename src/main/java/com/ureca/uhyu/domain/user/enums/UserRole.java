package com.ureca.uhyu.domain.user.enums;

public enum UserRole {
    ADMIN,
    USER,
    TMP_USER;

    public String getUserRole() {
        return this.name();
    }
}
