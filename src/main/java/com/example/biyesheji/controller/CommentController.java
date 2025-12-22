package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.dto.CommentCreateRequest;
import com.example.biyesheji.dto.CommentDTO;
import com.example.biyesheji.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@Tag(name = "评论接口")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "创建评论")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<CommentDTO> createComment(@Valid @RequestBody CommentCreateRequest request,
                                            Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        CommentDTO comment = commentService.createComment(userId, request);
        return Result.success("评论成功", comment);
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        commentService.deleteComment(id, userId);
        return Result.successWithMessage("删除成功");
    }

    @Operation(summary = "获取帖子的评论列表（分页）")
    @GetMapping("/post/{postId}")
    public Result<Page<CommentDTO>> getCommentsByPostId(@PathVariable Long postId,
                                                         @RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CommentDTO> comments = commentService.getCommentsByPostId(postId, page, pageSize);
        return Result.success(comments);
    }

    @Operation(summary = "获取帖子的所有评论（树形结构）")
    @GetMapping("/post/{postId}/tree")
    public Result<List<CommentDTO>> getCommentsTree(@PathVariable Long postId) {
        List<CommentDTO> comments = commentService.getCommentsTree(postId);
        return Result.success(comments);
    }

    @Operation(summary = "获取当前用户的评论列表（分页）")
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Page<CommentDTO>> getCommentsByUserId(@RequestParam(defaultValue = "1") Integer page,
                                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                                         Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Page<CommentDTO> comments = commentService.getCommentsByUserId(userId, page, pageSize);
        return Result.success(comments);
    }
}
