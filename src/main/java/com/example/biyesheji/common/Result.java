package com.example.biyesheji.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    // --- 修复后的最终版本 ---

    /**
     * 成功，返回数据
     * (最常用的方法)
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功，无返回数据
     * (用于删除、更新等操作)
     */
    public static Result<Void> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功，自定义消息，无返回数据
     * (用于返回特定成功消息，如 "删除成功")
     */
    public static Result<Void> successWithMessage(String message) {
        return new Result<>(200, message, null);
    }

    /**
     * 成功，自定义消息，并返回数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    // --- 错误处理方法 (保持不变) ---

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
