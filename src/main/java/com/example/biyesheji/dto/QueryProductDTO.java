package com.example.biyesheji.dto;

import com.example.biyesheji.enums.ProductCategory;
import com.example.biyesheji.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 查询商品DTO
 */
@Data
@Schema(description = "查询商品请求")
public class QueryProductDTO {

    @Schema(description = "商品分类", example = "BOOKS")
    private ProductCategory category;

    @Schema(description = "商品状态", example = "ON_SALE")
    private ProductStatus status;

    @Schema(description = "搜索关键词", example = "计算机")
    private String keyword;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "排序字段", example = "createTime")
    private String sortBy = "createTime";

    @Schema(description = "排序方向", example = "desc")
    private String sortOrder = "desc";

    @Schema(description = "是否只查询我的商品", example = "false")
    private Boolean my = false;

    @Schema(description = "用户ID", hidden = true)
    private Long userId;
}
