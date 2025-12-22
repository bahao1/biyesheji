package com.example.biyesheji.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 社团实体类
 */
@Data
@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 社团名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 社团简介
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 社团类型(体育/文艺/学术/公益等)
     */
    @Column(length = 50)
    private String type;

    /**
     * 社团logo
     */
    @Column(columnDefinition = "TEXT")
    private String logo;

    /**
     * 社团封面图
     */
    @Column(columnDefinition = "TEXT")
    private String cover;

    /**
     * 社长用户ID
     */
    @Column(nullable = true)
    private Long presidentId;

    /**
     * 社团成员数
     */
    @Column(nullable = false)
    private Integer memberCount = 0;

    /**
     * 社团状态(ACTIVE-活跃, SUSPENDED-暂停)
     */
    @Column(length = 20, nullable = false)
    private String status = "ACTIVE";

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
