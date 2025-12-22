package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.entity.Club;
import com.example.biyesheji.entity.Comment;
import com.example.biyesheji.entity.Post;
import com.example.biyesheji.entity.Product;
import com.example.biyesheji.entity.User;
import com.example.biyesheji.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 超级管理员Controller
 */
@Tag(name = "超级管理员管理")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "获取系统统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(adminService.getStatistics());
    }

    @Operation(summary = "查询用户列表")
    @GetMapping("/users")
    public Result<Page<User>> getUsers(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "角色") @RequestParam(required = false) String role,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(adminService.getUsers(keyword, role, page, pageSize));
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/users/{userId}/status")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "是否禁用") @RequestParam Boolean disabled
    ) {
        adminService.updateUserStatus(userId, disabled);
        return Result.success();
    }

    @Operation(summary = "查询所有帖子")
    @GetMapping("/posts")
    public Result<Page<Post>> getPosts(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(adminService.getPosts(keyword, page, pageSize));
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/posts/{postId}")
    public Result<Void> deletePost(@Parameter(description = "帖子ID") @PathVariable Long postId) {
        adminService.deletePost(postId);
        return Result.success();
    }

    @Operation(summary = "查询所有商品")
    @GetMapping("/products")
    public Result<Page<Product>> getProducts(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(adminService.getProducts(keyword, page, pageSize));
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/products/{productId}")
    public Result<Void> deleteProduct(@Parameter(description = "商品ID") @PathVariable Long productId) {
        adminService.deleteProduct(productId);
        return Result.success();
    }

    @Operation(summary = "查询所有评论")
    @GetMapping("/comments")
    public Result<Page<Comment>> getComments(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(adminService.getComments(keyword, page, pageSize));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@Parameter(description = "评论ID") @PathVariable Long commentId) {
        adminService.deleteComment(commentId);
        return Result.success();
    }

    @Operation(summary = "查询所有社团")
    @GetMapping("/clubs")
    public Result<Page<Club>> getClubs(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(adminService.getClubs(keyword, page, pageSize));
    }

    @Operation(summary = "创建社团")
    @PostMapping("/clubs")
    public Result<Club> createClub(@RequestBody Club club) {
        return Result.success(adminService.createClub(club));
    }

    @Operation(summary = "删除社团")
    @DeleteMapping("/clubs/{clubId}")
    public Result<Void> deleteClub(@Parameter(description = "社团ID") @PathVariable Long clubId) {
        adminService.deleteClub(clubId);
        return Result.success();
    }

    @Operation(summary = "分配社团管理员")
    @PutMapping("/users/{userId}/assign-club-admin")
    public Result<Void> assignClubAdmin(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "社团ID") @RequestParam Long clubId
    ) {
        adminService.assignClubAdmin(userId, clubId);
        return Result.success();
    }

    @Operation(summary = "取消社团管理员")
    @PutMapping("/users/{userId}/remove-club-admin")
    public Result<Void> removeClubAdmin(@Parameter(description = "用户ID") @PathVariable Long userId) {
        adminService.removeClubAdmin(userId);
        return Result.success();
    }
}
