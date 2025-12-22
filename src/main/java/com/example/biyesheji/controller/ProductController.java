package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.dto.CreateProductDTO;
import com.example.biyesheji.dto.QueryProductDTO;
import com.example.biyesheji.entity.Product;
import com.example.biyesheji.enums.ProductStatus;
import com.example.biyesheji.service.ProductService;
import com.example.biyesheji.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * 商品Controller
 */
@Tag(name = "商品管理", description = "二手交易商品相关接口")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "发布商品")
    @PostMapping("/create")
    public Result<Product> createProduct(@Valid @RequestBody CreateProductDTO dto,
                                         HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Product product = productService.createProduct(userId, dto);
        return Result.success(product);
    }

    @Operation(summary = "查询商品列表")
    @GetMapping("/query")
    public Result<Page<Product>> queryProducts(QueryProductDTO dto, HttpServletRequest request) {
        // 如果请求查询"我的商品"，设置当前用户ID
        if (dto.getMy() != null && dto.getMy()) {
            Long userId = jwtUtil.getUserIdFromRequest(request);
            dto.setUserId(userId);
        }
        Page<Product> products = productService.queryProducts(dto);
        return Result.success(products);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    public Result<Product> updateProduct(@PathVariable Long id,
                                         @Valid @RequestBody CreateProductDTO dto,
                                         HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Product product = productService.updateProduct(userId, id, dto);
        return Result.success(product);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id,
                                      HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        productService.deleteProduct(userId, id);
        return Result.successWithMessage("删除成功");
    }

    @Operation(summary = "更新商品状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateProductStatus(@PathVariable Long id,
                                            @RequestParam ProductStatus status,
                                            HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        productService.updateProductStatus(userId, id, status);
        return Result.successWithMessage("状态更新成功");
    }

    @Operation(summary = "获取用户的商品列表")
    @GetMapping("/user/{userId}")
    public Result<Page<Product>> getUserProducts(@PathVariable Long userId,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int pageSize) {
        Page<Product> products = productService.getUserProducts(userId, page, pageSize);
        return Result.success(products);
    }
}
