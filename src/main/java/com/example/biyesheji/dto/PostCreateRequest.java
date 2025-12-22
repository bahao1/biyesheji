package com.example.biyesheji.dto;

import com.example.biyesheji.enums.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 创建帖子请求DTO
 */
@Data
public class PostCreateRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "分类不能为空")
    private PostCategory category;

    private List<String> images;  // 图片URL列表

    private Boolean isAnonymous = false;  // 是否匿名发布
}
