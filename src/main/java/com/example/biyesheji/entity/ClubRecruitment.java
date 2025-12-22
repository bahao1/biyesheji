package com.example.biyesheji.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 社团招新实体类
 */
@Data
@Entity
@Table(name = "club_recruitments")
public class ClubRecruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 社团ID
     */
    @Column(nullable = false)
    private Long clubId;

    /**
     * 社团名称(冗余字段)
     */
    @Column(length = 100)
    private String clubName;

    /**
     * 招新标题
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 招新内容
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 招新要求
     */
    @Column(columnDefinition = "TEXT")
    private String requirements;

    /**
     * 招新人数
     */
    private Integer quota;

    /**
     * 报名截止时间
     */
    private LocalDateTime deadline;

    /**
     * 联系方式
     */
    @Column(length = 200)
    private String contact;

    /**
     * 浏览量
     */
    @Column(nullable = false)
    private Integer viewCount = 0;

    /**
     * 状态(OPEN-招新中, CLOSED-已结束)
     */
    @Column(length = 20, nullable = false)
    private String status = "OPEN";

    /**
     * 社长ID (不持久化到数据库，仅用于返回给前端)
     */
    @Transient
    private Long presidentId;

    /**
     * 是否删除
     */
    @Column(nullable = false)
    private Boolean deleted = false;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateTime;
}
