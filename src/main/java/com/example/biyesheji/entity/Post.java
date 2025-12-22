package com.example.biyesheji.entity;

import com.example.biyesheji.enums.PostCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 帖子实体类
 */
@Entity
@Table(name = "post")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostCategory category;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String images;  // 图片URL列表，用逗号分隔

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;  // 浏览次数

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;  // 点赞数

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;  // 评论数

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = false;  // 是否匿名

    @Column(name = "is_top", nullable = false)
    private Boolean isTop = false;  // 是否置顶

    @Column(nullable = false)
    private Boolean deleted = false;  // 是否删除（软删除）

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
