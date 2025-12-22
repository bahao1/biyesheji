package com.example.biyesheji.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.example.biyesheji.dto.*;
import com.example.biyesheji.entity.Post;
import com.example.biyesheji.entity.User;
import com.example.biyesheji.exception.BusinessException;
import com.example.biyesheji.repository.PostFavoriteRepository;
import com.example.biyesheji.repository.PostLikeRepository;
import com.example.biyesheji.repository.PostRepository;
import com.example.biyesheji.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 帖子服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostFavoriteRepository postFavoriteRepository;

    /**
     * 创建帖子
     */
    @Transactional
    public PostDTO createPost(Long userId, PostCreateRequest request) {
        // 验证用户是否存在
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 创建帖子
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setUserId(userId);
        post.setIsAnonymous(request.getIsAnonymous());

        // 处理图片列表
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            post.setImages(String.join(",", request.getImages()));
        }

        post = postRepository.save(post);
        log.info("用户 {} 创建帖子成功: {}", user.getUsername(), post.getId());

        return convertToDTO(post, user, false, false);
    }

    /**
     * 更新帖子
     */
    @Transactional
    public PostDTO updatePost(Long postId, Long userId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        // 验证权限
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权修改此帖子");
        }

        // 更新字段
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            post.setCategory(request.getCategory());
        }
        if (request.getImages() != null) {
            post.setImages(String.join(",", request.getImages()));
        }

        post = postRepository.save(post);
        log.info("帖子 {} 更新成功", postId);

        User user = userRepository.findById(userId).orElse(null);
        return convertToDTO(post, user, false, false);
    }

    /**
     * 删除帖子（软删除）
     */
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        // 验证权限
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此帖子");
        }

        post.setDeleted(true);
        postRepository.save(post);
        log.info("帖子 {} 删除成功", postId);
    }

    /**
     * 获取帖子详情
     */
    @Transactional
    public PostDTO getPostById(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        if (post.getDeleted()) {
            throw new BusinessException("帖子已被删除");
        }

        // 增加浏览次数
        postRepository.incrementViewCount(postId);

        // 获取用户信息
        User user = userRepository.findById(post.getUserId()).orElse(null);

        // 检查当前用户是否点赞和收藏
        boolean isLiked = false;
        boolean isFavorited = false;
        if (currentUserId != null) {
            isLiked = postLikeRepository.existsByPostIdAndUserId(postId, currentUserId);
            isFavorited = postFavoriteRepository.existsByPostIdAndUserId(postId, currentUserId);
        }

        return convertToDTO(post, user, isLiked, isFavorited);
    }

    /**
     * 查询帖子列表
     */
    public Page<PostDTO> queryPosts(PostQueryRequest request, Long currentUserId) {
        // 构建分页参数
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(request.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                request.getSortBy()
        );
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getPageSize(), sort);

        Page<Post> postPage;

        // 根据条件查询
        if (StrUtil.isNotBlank(request.getKeyword())) {
            // 关键词搜索
            postPage = postRepository.searchByKeyword(request.getKeyword(), pageable);
        } else if (request.getUserId() != null) {
            // 查询某个用户的帖子
            postPage = postRepository.findByUserIdAndDeletedFalseOrderByCreateTimeDesc(request.getUserId(), pageable);
        } else if (request.getCategory() != null) {
            // 按分类查询
            postPage = postRepository.findByCategoryAndDeletedFalseOrderByIsTopDescCreateTimeDesc(request.getCategory(), pageable);
        } else {
            // 查询所有
            postPage = postRepository.findByDeletedFalseOrderByIsTopDescCreateTimeDesc(pageable);
        }

        // 转换为DTO
        return postPage.map(post -> {
            User user = userRepository.findById(post.getUserId()).orElse(null);
            boolean isLiked = currentUserId != null && postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
            boolean isFavorited = currentUserId != null && postFavoriteRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
            return convertToDTO(post, user, isLiked, isFavorited);
        });
    }

    /**
     * 点赞帖子
     */
    @Transactional
    public void likePost(Long postId, Long userId) {
        // 验证帖子是否存在
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        if (post.getDeleted()) {
            throw new BusinessException("帖子已被删除");
        }

        // 检查是否已点赞
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new BusinessException("已经点赞过了");
        }

        // 创建点赞记录
        com.example.biyesheji.entity.PostLike postLike = new com.example.biyesheji.entity.PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        // 增加点赞数
        postRepository.incrementLikeCount(postId);
        log.info("用户 {} 点赞帖子 {}", userId, postId);
    }

    /**
     * 取消点赞
     */
    @Transactional
    public void unlikePost(Long postId, Long userId) {
        // 检查是否已点赞
        if (!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new BusinessException("还未点赞");
        }

        // 删除点赞记录
        postLikeRepository.deleteByPostIdAndUserId(postId, userId);

        // 减少点赞数
        postRepository.decrementLikeCount(postId);
        log.info("用户 {} 取消点赞帖子 {}", userId, postId);
    }

    /**
     * 收藏帖子
     */
    @Transactional
    public void favoritePost(Long postId, Long userId) {
        // 验证帖子是否存在
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException("帖子不存在"));

        if (post.getDeleted()) {
            throw new BusinessException("帖子已被删除");
        }

        // 检查是否已收藏
        if (postFavoriteRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new BusinessException("已经收藏过了");
        }

        // 创建收藏记录
        com.example.biyesheji.entity.PostFavorite postFavorite = new com.example.biyesheji.entity.PostFavorite();
        postFavorite.setPostId(postId);
        postFavorite.setUserId(userId);
        postFavoriteRepository.save(postFavorite);
        log.info("用户 {} 收藏帖子 {}", userId, postId);
    }

    /**
     * 取消收藏
     */
    @Transactional
    public void unfavoritePost(Long postId, Long userId) {
        // 检查是否已收藏
        if (!postFavoriteRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new BusinessException("还未收藏");
        }

        // 删除收藏记录
        postFavoriteRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("用户 {} 取消收藏帖子 {}", userId, postId);
    }

    /**
     * 获取用户收藏的帖子列表
     */
    public Page<PostDTO> getFavoritePosts(Long userId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<Long> postIds = postFavoriteRepository.findPostIdsByUserId(userId, pageable);

        return postIds.map(postId -> {
            Post post = postRepository.findById(postId).orElse(null);
            if (post == null || post.getDeleted()) {
                return null;
            }
            User user = userRepository.findById(post.getUserId()).orElse(null);
            return convertToDTO(post, user, true, true);
        });
    }

    /**
     * 获取某个用户发布的所有帖子
     */
    public Page<PostDTO> getUserPosts(Long userId, Integer page, Integer pageSize, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Post> posts = postRepository.findByUserIdAndDeletedFalse(userId, pageable);

        return posts.map(post -> {
            User user = userRepository.findById(post.getUserId()).orElse(null);
            boolean isLiked = currentUserId != null && postLikeRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
            boolean isFavorited = currentUserId != null && postFavoriteRepository.existsByPostIdAndUserId(post.getId(), currentUserId);
            return convertToDTO(post, user, isLiked, isFavorited);
        });
    }

    /**
     * 实体转DTO
     */
    private PostDTO convertToDTO(Post post, User user, boolean isLiked, boolean isFavorited) {
        PostDTO dto = new PostDTO();
        BeanUtil.copyProperties(post, dto);

        // 处理图片列表
        if (StrUtil.isNotBlank(post.getImages())) {
            dto.setImages(List.of(post.getImages().split(",")));
        }

        // 设置用户信息（如果是匿名帖子则隐藏）
        if (post.getIsAnonymous()) {
            dto.setUsername("匿名用户");
            dto.setNickname("匿名用户");
            dto.setAvatar(null);
        } else if (user != null) {
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setAvatar(user.getAvatar());
        }

        dto.setIsLiked(isLiked);
        dto.setIsFavorited(isFavorited);

        return dto;
    }
}
