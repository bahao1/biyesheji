package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI功能Controller
 */
@Tag(name = "AI功能")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @Operation(summary = "生成表白情书")
    @PostMapping("/love-letter")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<String> generateLoveLetter(@RequestBody LoveLetterRequest request) {
        String result = aiService.generateLoveLetter(
                request.getTargetName(),
                request.getKeywords(),
                request.getStyle()
        );
        return Result.success(result);
    }

    @Operation(summary = "生成藏头诗")
    @PostMapping("/acrostic-poem")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<String> generateAcrosticPoem(@RequestBody AcrosticPoemRequest request) {
        String result = aiService.generateAcrosticPoem(request.getText());
        return Result.success(result);
    }

    @Operation(summary = "校园助手问答")
    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<String> chat(@RequestBody ChatRequest request) {
        String result = aiService.chatAssistant(
                request.getQuestion(),
                request.getHistory()
        );
        return Result.success(result);
    }

    /**
     * 表白情书请求
     */
    @Data
    public static class LoveLetterRequest {
        private String targetName;      // 对方名字
        private List<String> keywords;  // 关键词
        private String style;           // 风格: funny(沙雕) romantic(深情) cute(土味)
    }

    /**
     * 藏头诗请求
     */
    @Data
    public static class AcrosticPoemRequest {
        private String text;  // 要藏的文字(2-4个字)
    }

    /**
     * 聊天请求
     */
    @Data
    public static class ChatRequest {
        private String question;                    // 问题
        private List<Map<String, String>> history;  // 对话历史
    }
}
