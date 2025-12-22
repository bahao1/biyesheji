package com.example.biyesheji.entity;

import com.example.biyesheji.enums.ProductCategory;
import com.example.biyesheji.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品标题
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 商品描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 商品分类
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    /**
     * 价格
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * 原价(用于显示折扣)
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal originalPrice;

    /**
     * 卖家ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 卖家昵称(冗余字段,方便查询)
     */
    @Column(length = 50)
    private String sellerNickname;

    /**
     * 商品图片(多张图片用逗号分隔)
     */
    @Column(columnDefinition = "TEXT")
    private String images;

    /**
     * 商品状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ON_SALE;

    /**
     * 浏览量
     */
    @Column(nullable = false)
    private Integer viewCount = 0;

    /**
     * 收藏量
     */
    @Column(nullable = false)
    private Integer favoriteCount = 0;

    /**
     * 联系方式
     */
    @Column(length = 100)
    private String contact;

    /**
     * 交易地点
     */
    @Column(length = 200)
    private String location;

    /**
     * 成色描述
     */
    @Column(name = "product_condition", length = 50)
    private String productCondition;

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
