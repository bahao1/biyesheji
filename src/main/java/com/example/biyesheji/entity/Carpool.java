package com.example.biyesheji.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 拼车信息实体
 */
@Data
@Entity
@Table(name = "carpools")
public class Carpool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发布者ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 出发地
     */
    @Column(nullable = false, length = 200)
    private String departurePlace;

    /**
     * 目的地
     */
    @Column(nullable = false, length = 200)
    private String destination;

    /**
     * 出发时间
     */
    @Column(nullable = false)
    private LocalDateTime departureTime;

    /**
     * 座位数
     */
    @Column(nullable = false)
    private Integer seats;

    /**
     * 已参与人数（用于显示x/总座位数）
     */
    @Column(nullable = false)
    private Integer remainingSeats;

    /**
     * 价格(每人)
     */
    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerPerson;

    /**
     * 联系方式
     */
    @Column(length = 100)
    private String contact;

    /**
     * 备注信息
     */
    @Column(columnDefinition = "TEXT")
    private String remarks;

    /**
     * 状态: OPEN(开放), FULL(已满), CLOSED(已关闭)
     */
    @Column(length = 20, nullable = false)
    private String status = "OPEN";

    /**
     * 拼车类型: DRIVER(车主找乘客), PASSENGER(乘客找车主)
     */
    @Column(length = 20, nullable = false)
    private String type;

    /**
     * 浏览次数
     */
    @Column(nullable = false)
    private Integer viewCount = 0;

    /**
     * 是否删除
     */
    @Column(nullable = false)
    private Boolean deleted = false;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    private LocalDateTime updateTime;
}
