package com.example.biyesheji.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论信息DTO
 */
@Data
public class CommentDTO {

    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String content;
    private Long parentId;
    private Long replyToUserId;
    private String replyToUsername;  // 回复的用户名
    private String replyToNickname;  // 回复的用户昵称
    private Integer likeCount;
    private LocalDateTime createTime;
    private List<CommentDTO> replies;  // 子评论列表
    
    // 关联帖子信息（用于"我的评论"页面）
    private String postTitle;    // 帖子标题
    private String postContent;  // 帖子内容
}
