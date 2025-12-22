package com.example.biyesheji.dto;

import com.example.biyesheji.enums.PostCategory;
import lombok.Data;

/**
 * 帖子查询请求DTO
 */
@Data
public class PostQueryRequest {

    private PostCategory category;  // 分类筛选

    private String keyword;  // 关键词搜索（标题或内容）

    private Long userId;  // 用户ID（查询某个用户的帖子）

    private String sortBy = "createTime";  // 排序字段：createTime, viewCount, likeCount

    private String sortOrder = "desc";  // 排序方向：asc, desc

    private Integer page = 1;  // 页码

    private Integer pageSize = 10;  // 每页数量
}
