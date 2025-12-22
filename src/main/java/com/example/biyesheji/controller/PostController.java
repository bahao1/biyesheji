package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.dto.*;
import com.example.biyesheji.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 帖子控制器
 */
@Tag(name = "帖子接口")
@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "创建帖子")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<PostDTO> createPost(@Valid @RequestBody PostCreateRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        PostDTO post = postService.createPost(userId, request);
        return Result.success("发布成功", post);
    }

    @Operation(summary = "更新帖子")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<PostDTO> updatePost(@PathVariable Long id,
                                      @Valid @RequestBody PostUpdateRequest request,
                                      Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        PostDTO post = postService.updatePost(id, userId, request);
        return Result.success("更新成功", post);
    }

    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        postService.deletePost(id, userId);
        return Result.successWithMessage("删除成功");
    }

    @Operation(summary = "获取帖子详情")
    @GetMapping("/{id}")
    public Result<PostDTO> getPostById(@PathVariable Long id, Authentication authentication) {
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        PostDTO post = postService.getPostById(id, userId);
        return Result.success(post);
    }

    @Operation(summary = "查询帖子列表")
    @PostMapping("/query")
    public Result<Page<PostDTO>> queryPosts(@RequestBody PostQueryRequest request, Authentication authentication) {
        Long userId = authentication != null ? (Long) authentication.getPrincipal() : null;
        Page<PostDTO> posts = postService.queryPosts(request, userId);
        return Result.success(posts);
    }

    @Operation(summary = "点赞帖子")
    @PostMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> likePost(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        postService.likePost(id, userId);
        return Result.successWithMessage("点赞成功");
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/{id}/like")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> unlikePost(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        postService.unlikePost(id, userId);
        return Result.successWithMessage("取消点赞成功");
    }

    @Operation(summary = "收藏帖子")
    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> favoritePost(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        postService.favoritePost(id, userId);
        return Result.successWithMessage("收藏成功");
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> unfavoritePost(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        postService.unfavoritePost(id, userId);
        return Result.successWithMessage("取消收藏成功");
    }

    @Operation(summary = "获取我的收藏列表")
    @GetMapping("/favorites")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Page<PostDTO>> getFavoritePosts(@RequestParam(defaultValue = "1") Integer page,
                                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                                   Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Page<PostDTO> posts = postService.getFavoritePosts(userId, page, pageSize);
        return Result.success(posts);
    }

    @Operation(summary = "获取某个用户发布的所有帖子")
    @GetMapping("/user/{userId}")
    public Result<Page<PostDTO>> getUserPosts(@PathVariable Long userId,
                                               @RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "10") Integer pageSize,
                                               Authentication authentication) {
        Long currentUserId = authentication != null ? (Long) authentication.getPrincipal() : null;
        Page<PostDTO> posts = postService.getUserPosts(userId, page, pageSize, currentUserId);
        return Result.success(posts);
    }
}
