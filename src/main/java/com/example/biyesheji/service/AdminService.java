package com.example.biyesheji.service;

import com.example.biyesheji.entity.Club;
import com.example.biyesheji.entity.Comment;
import com.example.biyesheji.entity.Post;
import com.example.biyesheji.entity.Product;
import com.example.biyesheji.entity.User;
import com.example.biyesheji.enums.UserRole;
import com.example.biyesheji.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 超级管理员Service
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ProductRepository productRepository;
    private final CommentRepository commentRepository;
    private final ClubRepository clubRepository;

    /**
     * 获取系统统计数据
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 用户统计
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByDisabledFalse();

        // 帖子统计
        long totalPosts = postRepository.count();
        long activePosts = postRepository.countByDeletedFalse();

        // 商品统计
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByDeletedFalse();

        // 评论统计
        long totalComments = commentRepository.count();
        long activeComments = commentRepository.countByDeletedFalse();

        statistics.put("totalUsers", totalUsers);
        statistics.put("activeUsers", activeUsers);
        statistics.put("totalPosts", totalPosts);
        statistics.put("activePosts", activePosts);
        statistics.put("totalProducts", totalProducts);
        statistics.put("activeProducts", activeProducts);
        statistics.put("totalComments", totalComments);
        statistics.put("activeComments", activeComments);

        return statistics;
    }

    /**
     * 查询用户列表
     */
    public Page<User> getUsers(String keyword, String role, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        if (keyword != null && !keyword.isEmpty() && role != null && !role.isEmpty()) {
            UserRole userRole = UserRole.valueOf(role);
            return userRepository.findByUsernameContainingOrNicknameContainingAndRole(
                    keyword, keyword, userRole, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            return userRepository.findByUsernameContainingOrNicknameContaining(
                    keyword, keyword, pageable);
        } else if (role != null && !role.isEmpty()) {
            UserRole userRole = UserRole.valueOf(role);
            return userRepository.findByRole(userRole, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    /**
     * 更新用户状态
     */
    @Transactional
    public void updateUserStatus(Long userId, Boolean disabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setDisabled(disabled);
        userRepository.save(user);
    }

    /**
     * 分配社团管理员
     */
    @Transactional
    public void assignClubAdmin(Long userId, Long clubId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setRole(com.example.biyesheji.enums.UserRole.CLUB_ADMIN);
        user.setClubId(clubId);
        userRepository.save(user);
    }

    /**
     * 取消社团管理员
     */
    @Transactional
    public void removeClubAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setRole(com.example.biyesheji.enums.UserRole.USER);
        user.setClubId(null);
        userRepository.save(user);
    }

    /**
     * 查询所有帖子
     */
    public Page<Post> getPosts(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        if (keyword != null && !keyword.isEmpty()) {
            return postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        } else {
            return postRepository.findAll(pageable);
        }
    }

    /**
     * 删除帖子
     */
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        post.setDeleted(true);
        postRepository.save(post);
    }

    /**
     * 查询所有商品
     */
    public Page<Product> getProducts(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        if (keyword != null && !keyword.isEmpty()) {
            return productRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    /**
     * 删除商品
     */
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        product.setDeleted(true);
        productRepository.save(product);
    }

    /**
     * 查询所有评论
     */
    public Page<Comment> getComments(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        if (keyword != null && !keyword.isEmpty()) {
            return commentRepository.findByContentContaining(keyword, pageable);
        } else {
            return commentRepository.findAll(pageable);
        }
    }

    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    /**
     * 查询所有社团
     */
    public Page<Club> getClubs(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        if (keyword != null && !keyword.isEmpty()) {
            return clubRepository.findByNameContainingAndDeletedFalse(keyword, pageable);
        }
        return clubRepository.findByDeletedFalse(pageable);
    }

    /**
     * 创建社团
     */
    @Transactional
    public Club createClub(Club club) {
        return clubRepository.save(club);
    }

    /**
     * 删除社团
     */
    @Transactional
    public void deleteClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("社团不存在"));
        club.setDeleted(true);
        clubRepository.save(club);
    }
}
