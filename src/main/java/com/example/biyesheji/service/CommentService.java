package com.example.biyesheji.service;

import cn.hutool.core.bean.BeanUtil;
import com.example.biyesheji.dto.CommentCreateRequest;
import com.example.biyesheji.dto.CommentDTO;
import com.example.biyesheji.entity.Comment;
import com.example.biyesheji.entity.Post;
import com.example.biyesheji.entity.User;
import com.example.biyesheji.exception.BusinessException;
import com.example.biyesheji.repository.CommentRepository;
import com.example.biyesheji.repository.PostRepository;
import com.example.biyesheji.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 创建评论
     */
    @Transactional
    public CommentDTO createComment(Long userId, CommentCreateRequest request) {
        // 验证帖子是否存在
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        if (post.getDeleted()) {
            throw new BusinessException("帖子已被删除");
        }

        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 如果是回复评论，验证父评论是否存在
        if (request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new BusinessException("父评论不存在"));
            if (parentComment.getDeleted()) {
                throw new BusinessException("父评论已被删除");
            }
        }

        // 创建评论
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setReplyToUserId(request.getReplyToUserId());

        comment = commentRepository.save(comment);

        // 增加帖子评论数
        postRepository.incrementCommentCount(request.getPostId());

        log.info("用户 {} 评论帖子 {}", user.getUsername(), request.getPostId());

        return convertToDTO(comment, user, null);
    }

    /**
     * 删除评论（软删除）
     */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException("评论不存在"));

        // 验证权限
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);

        // 减少帖子评论数
        postRepository.decrementCommentCount(comment.getPostId());

        log.info("评论 {} 删除成功", commentId);
    }

    /**
     * 获取帖子的评论列表（分页）
     */
    public Page<CommentDTO> getCommentsByPostId(Long postId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Comment> commentPage = commentRepository.findByPostIdAndDeletedFalseOrderByCreateTimeDesc(postId, pageable);

        return commentPage.map(comment -> {
            User user = userRepository.findById(comment.getUserId()).orElse(null);
            User replyToUser = null;
            if (comment.getReplyToUserId() != null) {
                replyToUser = userRepository.findById(comment.getReplyToUserId()).orElse(null);
            }
            return convertToDTO(comment, user, replyToUser);
        });
    }

    /**
     * 获取帖子的所有评论（包含子评论，树形结构）
     */
    public List<CommentDTO> getCommentsTree(Long postId) {
        // 获取所有评论
        List<Comment> allComments = commentRepository.findByPostIdAndDeletedFalseOrderByCreateTimeAsc(postId);

        // 获取所有用户信息
        List<Long> userIds = allComments.stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 获取回复用户信息
        List<Long> replyToUserIds = allComments.stream()
                .filter(c -> c.getReplyToUserId() != null)
                .map(Comment::getReplyToUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> replyToUserMap = userRepository.findAllById(replyToUserIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // 转换为DTO
        List<CommentDTO> commentDTOs = allComments.stream()
                .map(comment -> {
                    User user = userMap.get(comment.getUserId());
                    User replyToUser = comment.getReplyToUserId() != null ?
                            replyToUserMap.get(comment.getReplyToUserId()) : null;
                    return convertToDTO(comment, user, replyToUser);
                })
                .collect(Collectors.toList());

        // 构建树形结构
        Map<Long, CommentDTO> commentMap = commentDTOs.stream()
                .collect(Collectors.toMap(CommentDTO::getId, dto -> dto));

        List<CommentDTO> rootComments = new ArrayList<>();
        for (CommentDTO dto : commentDTOs) {
            if (dto.getParentId() == null) {
                // 根评论
                rootComments.add(dto);
            } else {
                // 子评论
                CommentDTO parent = commentMap.get(dto.getParentId());
                if (parent != null) {
                    if (parent.getReplies() == null) {
                        parent.setReplies(new ArrayList<>());
                    }
                    parent.getReplies().add(dto);
                }
            }
        }

        return rootComments;
    }

    /**
     * 获取用户的评论列表（分页）
     */
    public Page<CommentDTO> getCommentsByUserId(Long userId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Comment> commentPage = commentRepository.findByUserIdAndDeletedFalseOrderByCreateTimeDesc(userId, pageable);

        return commentPage.map(comment -> {
            User user = userRepository.findById(comment.getUserId()).orElse(null);
            User replyToUser = null;
            if (comment.getReplyToUserId() != null) {
                replyToUser = userRepository.findById(comment.getReplyToUserId()).orElse(null);
            }
            
            CommentDTO dto = convertToDTO(comment, user, replyToUser);
            
            // 添加关联帖子信息
            Post post = postRepository.findById(comment.getPostId()).orElse(null);
            if (post != null) {
                dto.setPostTitle(post.getTitle());
                dto.setPostContent(post.getContent());
            }
            
            return dto;
        });
    }

    /**
     * 实体转DTO
     */
    private CommentDTO convertToDTO(Comment comment, User user, User replyToUser) {
        CommentDTO dto = new CommentDTO();
        BeanUtil.copyProperties(comment, dto);

        if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setAvatar(user.getAvatar());
        }

        if (replyToUser != null) {
            dto.setReplyToUsername(replyToUser.getUsername());
            dto.setReplyToNickname(replyToUser.getNickname());
        }

        return dto;
    }
}
