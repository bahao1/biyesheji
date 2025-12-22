package com.example.biyesheji.dto;

import com.example.biyesheji.enums.ProductCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建商品DTO
 */
@Data
@Schema(description = "创建商品请求")
public class CreateProductDTO {

    @Schema(description = "商品标题", example = "计算机网络第8版")
    @NotBlank(message = "商品标题不能为空")
    private String title;

    @Schema(description = "商品描述", example = "九成新,没有笔记,质量很好")
    private String description;

    @Schema(description = "商品分类", example = "BOOKS")
    @NotNull(message = "商品分类不能为空")
    private ProductCategory category;

    @Schema(description = "价格", example = "25.00")
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    @Schema(description = "原价", example = "50.00")
    private BigDecimal originalPrice;

    @Schema(description = "商品图片URL列表", example = "[\"https://example.com/img1.jpg\",\"https://example.com/img2.jpg\"]")
    @JsonProperty("images")
    private List<String> images;

    @Schema(description = "联系方式", example = "微信: wxid123")
    private String contact;

    @Schema(description = "交易地点", example = "学校东门")
    private String location;

    @Schema(description = "成色描述", example = "九成新")
    private String productCondition;

    /**
     * 获取图片字符串（逗号分隔）- 用于保存到数据库
     */
    @JsonIgnore
    public String getImagesString() {
        if (images == null || images.isEmpty()) {
            return "";
        }
        return String.join(",", images);
    }
}
