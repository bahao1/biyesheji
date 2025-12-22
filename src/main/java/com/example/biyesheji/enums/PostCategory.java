package com.example.biyesheji.enums;

import lombok.Getter;

/**
 * 帖子分类枚举
 */
@Getter
public enum PostCategory {
    GENERAL("普通帖子"),
    CONFESSION("表白墙"),
    LOST_FOUND("失物招领"),
    HELP("求助"),
    ACTIVITY("活动"),
    STUDY("学习交流"),
    LIFE("生活分享"),
    OTHER("其他");

    private final String description;

    PostCategory(String description) {
        this.description = description;
    }
}
