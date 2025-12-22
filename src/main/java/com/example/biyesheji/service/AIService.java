package com.example.biyesheji.service;

import com.example.biyesheji.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * AI服务 - 硅基流动 Qwen2.5-7B微调模型
 */
@Slf4j
@Service
public class AIService {

    @Value("${ai.api.key:}")
    private String apiKey;

    @Value("${ai.api.url:https://api.siliconflow.cn/v1/chat/completions}")
    private String apiUrl;

    @Value("${ai.api.model:ft:LoRA/Qwen/Qwen2.5-7B-Instruct:xfqjbhsr1c:xue:rutqabyamrvqhvejmgoe-ckpt_step_1}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 生成情书
     */
    public String generateLoveLetter(String targetName, List<String> keywords, String style) {
        log.info("API Key配置状态: {}", (apiKey == null || apiKey.isEmpty()) ? "未配置" : "已配置(长度:" + apiKey.length() + ")");
        log.info("API URL: {}", apiUrl);
        log.info("AI Model: {}", model);

        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("API Key未配置，使用Mock数据");
            return generateMockLoveLetter(targetName, keywords, style);
        }

        String prompt = buildLoveLetterPrompt(targetName, keywords, style);
        return callAI(prompt);
    }

    /**
     * 生成藏头诗
     */
    public String generateAcrosticPoem(String text) {
        if (apiKey == null || apiKey.isEmpty()) {
            return generateMockAcrosticPoem(text);
        }

        String prompt = String.format("请以'%s'为藏头，生成一首浪漫的藏头诗，每句开头的字连起来刚好是'%s'。" +
                "要求：富有诗意、浪漫动人、适合表白场景。直接输出诗句，不要其他说明。", text, text);
        return callAI(prompt);
    }

    /**
     * 校园助手问答
     */
    public String chatAssistant(String question, List<Map<String, String>> history) {
        if (apiKey == null || apiKey.isEmpty()) {
            return generateMockAnswer(question);
        }

        String prompt = "你是一个友好的校园助手,专门回答关于校园生活的问题(食堂、教学楼位置、图书馆开放时间、课程安排等)。" +
                "请用简洁、友好的语气回答学生的问题。\n\n学生问题: " + question;
        return callAI(prompt);
    }

    /**
     * 调用硅基流动API (OpenAI兼容格式)
     */
    private String callAI(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 构建OpenAI格式的请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);

            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个友好、有创意的AI助手，擅长写作和创意表达。");
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 500);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.info("调用AI API: {}", apiUrl);
            log.debug("请求体: {}", requestBody);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(apiUrl, request, Map.class);

            log.debug("AI响应: {}", response);

            if (response != null && response.containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null && message.containsKey("content")) {
                        return (String) message.get("content");
                    }
                }
            }

            log.error("AI响应格式错误: {}", response);
            throw new BusinessException("AI响应格式错误");
        } catch (Exception e) {
            log.error("调用AI接口失败", e);
            throw new BusinessException("AI服务暂时不可用: " + e.getMessage());
        }
    }

    /**
     * 构建情书Prompt
     */
    private String buildLoveLetterPrompt(String targetName, List<String> keywords, String style) {
        String styleDesc = switch (style) {
            case "funny" -> "幽默沙雕、轻松搞笑";
            case "romantic" -> "深情浪漫、真挚感人";
            case "cute" -> "土味情话、甜蜜可爱";
            default -> "真诚温暖";
        };

        return String.format("请为我生成一封表白信，收信人是'%s'，ta的特点是：%s。" +
                        "要求：风格为%s，字数控制在150-200字，要真诚动人，适合校园表白场景。" +
                        "直接输出表白内容，不要有标题或其他说明。",
                targetName, String.join("、", keywords), styleDesc);
    }

    /**
     * Mock情书生成(无API Key时使用)
     */
    private String generateMockLoveLetter(String targetName, List<String> keywords, String style) {
        String keywordStr = keywords.isEmpty() ? "可爱" : keywords.get(0);
        return switch (style) {
            case "funny" -> String.format("亲爱的%s:\n\n自从遇见你，我的心跳就像校园网一样不稳定。" +
                    "你的%s让我沦陷，就像食堂的红烧肉一样让人无法抗拒！\n\n" +
                    "做我女朋友吧，我请你吃一个月的食堂！", targetName, keywordStr);
            case "romantic" -> String.format("致%s:\n\n在人来人往的校园里，" +
                    "你的%s如同星光般照亮了我的世界。每一次相遇都是命运的安排，" +
                    "每一个微笑都深深印在我心底。\n\n愿与你共度春夏秋冬。", targetName, keywordStr);
            default -> String.format("%s，你知道吗？我对你的喜欢，就像圆周率一样，永不循环，无限不循环！" +
                    "你的%s让我心动不已。做我的女朋友好不好？", targetName, keywordStr);
        };
    }

    /**
     * Mock藏头诗生成
     */
    private String generateMockAcrosticPoem(String text) {
        return text.chars()
                .mapToObj(c -> String.valueOf((char) c) + "字开头的诗句...(请配置AI API Key获得真实藏头诗)")
                .reduce((a, b) -> a + "\n" + b)
                .orElse("请输入2-4个字");
    }

    /**
     * Mock问答
     */
    private String generateMockAnswer(String question) {
        if (question.contains("食堂")) {
            return "食堂一般11:00-13:00供应午餐，17:00-19:00供应晚餐。推荐二食堂的麻辣香锅和三食堂的黄焖鸡！";
        } else if (question.contains("图书馆")) {
            return "图书馆开放时间：\n周一至周五 8:00-22:00\n周末 9:00-21:00\n期末周会延长到23:00哦！";
        } else if (question.contains("教学楼") || question.contains("怎么走")) {
            return "可以使用校园地图App查看路线，或者询问校门口的保安叔叔，他们最熟悉校园路线啦！";
        }
        return "这个问题我还在学习中~请配置AI API Key以获得更智能的回答！";
    }
}
