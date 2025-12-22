package com.example.biyesheji.enums;

import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    USER("普通用户"),
    CLUB_ADMIN("社团管理员"),
    SUPER_ADMIN("超级管理员");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }
}
