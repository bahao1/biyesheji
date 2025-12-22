package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.entity.Message;
import com.example.biyesheji.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 私信消息Controller
 */
@Tag(name = "私信消息")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "发送私信")
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Message> sendMessage(@RequestBody SendMessageRequest request, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        Message message = messageService.sendMessage(
                currentUserId,
                request.getReceiverId(),
                request.getContent()
        );
        return Result.success(message);
    }

    @Operation(summary = "获取聊天记录")
    @GetMapping("/chat/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<List<Message>> getChatHistory(@PathVariable Long userId, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        List<Message> messages = messageService.getChatHistory(currentUserId, userId);
        return Result.success(messages);
    }

    @Operation(summary = "获取会话列表")
    @GetMapping("/conversations")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<List<Map<String, Object>>> getConversationList(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        List<Map<String, Object>> conversations = messageService.getConversationList(currentUserId);
        return Result.success(conversations);
    }

    @Operation(summary = "标记消息为已读")
    @PutMapping("/read/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> markAsRead(@PathVariable Long userId, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        messageService.markAsRead(userId, currentUserId);
        return Result.success();
    }

    @Operation(summary = "获取未读消息总数")
    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Long> getUnreadCount(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        Long count = messageService.getUnreadCount(currentUserId);
        return Result.success(count);
    }

    @Operation(summary = "获取与某用户的未读消息数")
    @GetMapping("/unread-count/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Long> getUnreadCountWithUser(@PathVariable Long userId, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        Long count = messageService.getUnreadCountWithUser(currentUserId, userId);
        return Result.success(count);
    }

    /**
     * 发送消息请求
     */
    @Data
    public static class SendMessageRequest {
        private Long receiverId;    // 接收者ID
        private String content;      // 消息内容
    }
}
