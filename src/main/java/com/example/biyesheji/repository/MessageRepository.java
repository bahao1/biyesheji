package com.example.biyesheji.repository;

import com.example.biyesheji.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 私信消息Repository
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * 查询两个用户之间的聊天记录
     */
    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
            "ORDER BY m.createTime ASC")
    List<Message> findChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 查询用户的所有会话列表(最后一条消息)
     */
    @Query(value = "SELECT * FROM message m1 WHERE m1.id IN ( " +
            "SELECT MAX(m2.id) FROM message m2 WHERE " +
            "m2.sender_id = :userId OR m2.receiver_id = :userId " +
            "GROUP BY CASE " +
            "WHEN m2.sender_id = :userId THEN m2.receiver_id " +
            "ELSE m2.sender_id END) " +
            "ORDER BY m1.create_time DESC", nativeQuery = true)
    List<Message> findConversationList(@Param("userId") Long userId);

    /**
     * 统计未读消息数
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.isRead = false")
    Long countUnreadMessages(@Param("userId") Long userId);

    /**
     * 标记消息为已读
     */
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.senderId = :senderId AND m.receiverId = :receiverId AND m.isRead = false")
    void markAsRead(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    /**
     * 查询两个用户之间的未读消息数
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.senderId = :senderId AND m.receiverId = :receiverId AND m.isRead = false")
    Long countUnreadBetweenUsers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}
