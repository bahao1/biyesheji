package com.example.biyesheji.service;

import com.example.biyesheji.entity.Message;
import com.example.biyesheji.entity.User;
import com.example.biyesheji.exception.BusinessException;
import com.example.biyesheji.repository.MessageRepository;
import com.example.biyesheji.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 私信消息Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    /**
     * 发送私信
     */
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        // 验证用户存在
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException("发送者不存在"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException("接收者不存在"));

        if (senderId.equals(receiverId)) {
            throw new BusinessException("不能给自己发送消息");
        }

        // 创建消息
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(false);
        message.setSenderNickname(sender.getNickname());
        message.setReceiverNickname(receiver.getNickname());

        return messageRepository.save(message);
    }

    /**
     * 获取聊天记录
     */
    public List<Message> getChatHistory(Long userId1, Long userId2) {
        return messageRepository.findChatHistory(userId1, userId2);
    }

    /**
     * 获取会话列表(显示与每个用户的最后一条消息)
     */
    public List<Map<String, Object>> getConversationList(Long userId) {
        List<Message> messages = messageRepository.findConversationList(userId);

        List<Map<String, Object>> conversations = new ArrayList<>();
        for (Message message : messages) {
            Map<String, Object> conversation = new HashMap<>();

            // 确定对方是谁
            Long otherUserId;
            String otherUserNickname;
            if (message.getSenderId().equals(userId)) {
                otherUserId = message.getReceiverId();
                otherUserNickname = message.getReceiverNickname();
            } else {
                otherUserId = message.getSenderId();
                otherUserNickname = message.getSenderNickname();
            }

            conversation.put("userId", otherUserId);
            conversation.put("nickname", otherUserNickname);
            conversation.put("lastMessage", message.getContent());
            conversation.put("lastMessageTime", message.getCreateTime());
            conversation.put("isRead", message.getIsRead());

            // 统计未读消息数(对方发给我的未读消息)
            Long unreadCount = messageRepository.countUnreadBetweenUsers(otherUserId, userId);
            conversation.put("unreadCount", unreadCount);

            conversations.add(conversation);
        }

        return conversations;
    }

    /**
     * 标记消息为已读
     */
    @Transactional
    public void markAsRead(Long senderId, Long receiverId) {
        messageRepository.markAsRead(senderId, receiverId);
    }

    /**
     * 获取未读消息总数
     */
    public Long getUnreadCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    /**
     * 获取与某用户的未读消息数
     */
    public Long getUnreadCountWithUser(Long userId, Long otherUserId) {
        return messageRepository.countUnreadBetweenUsers(otherUserId, userId);
    }
}
