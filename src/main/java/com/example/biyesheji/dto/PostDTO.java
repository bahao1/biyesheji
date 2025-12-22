package com.example.biyesheji.dto;

import com.example.biyesheji.enums.PostCategory;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子信息DTO
 */
@Data
public class PostDTO {

    private Long id;
    private String title;
    private String content;
    private PostCategory category;
    private Long userId;
    private String username;  // 发帖人用户名
    private String nickname;  // 发帖人昵称
    private String avatar;    // 发帖人头像
    private List<String> images;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isAnonymous;
    private Boolean isTop;
    private Boolean isLiked;  // 当前用户是否点赞
    private Boolean isFavorited;  // 当前用户是否收藏
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
