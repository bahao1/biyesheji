package com.example.biyesheji.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建评论请求DTO
 */
@Data
public class CommentCreateRequest {

    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Long parentId;  // 父评论ID（回复评论时使用）

    private Long replyToUserId;  // 回复的用户ID
}
