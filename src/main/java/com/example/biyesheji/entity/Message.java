package com.example.biyesheji.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 私信消息实体
 */
@Data
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发送者ID
     */
    @Column(nullable = false)
    private Long senderId;

    /**
     * 接收者ID
     */
    @Column(nullable = false)
    private Long receiverId;

    /**
     * 消息内容
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 是否已读
     */
    @Column(nullable = false)
    private Boolean isRead = false;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 发送者昵称(冗余字段,用于查询优化)
     */
    @Column(length = 50)
    private String senderNickname;

    /**
     * 接收者昵称(冗余字段,用于查询优化)
     */
    @Column(length = 50)
    private String receiverNickname;
}
