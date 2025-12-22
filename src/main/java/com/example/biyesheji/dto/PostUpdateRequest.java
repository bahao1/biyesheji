package com.example.biyesheji.dto;

import com.example.biyesheji.enums.PostCategory;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 更新帖子请求DTO
 */
@Data
public class PostUpdateRequest {

    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    private String content;

    private PostCategory category;

    private List<String> images;
}
