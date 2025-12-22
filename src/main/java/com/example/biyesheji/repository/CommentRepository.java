package com.example.biyesheji.repository;

import com.example.biyesheji.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论数据访问层
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 根据帖子ID查询评论（未删除，分页）
     */
    Page<Comment> findByPostIdAndDeletedFalseOrderByCreateTimeDesc(Long postId, Pageable pageable);

    /**
     * 根据帖子ID查询所有评论（未删除）
     */
    List<Comment> findByPostIdAndDeletedFalseOrderByCreateTimeAsc(Long postId);

    /**
     * 根据父评论ID查询子评论（未删除）
     */
    List<Comment> findByParentIdAndDeletedFalseOrderByCreateTimeAsc(Long parentId);

    /**
     * 统计帖子的评论数（未删除）
     */
    long countByPostIdAndDeletedFalse(Long postId);

    /**
     * 增加点赞数
     */
    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞数
     */
    @Modifying
    @Query("UPDATE Comment c SET c.likeCount = c.likeCount - 1 WHERE c.id = :id AND c.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);

    /**
     * 统计未删除的评论数量
     */
    long countByDeletedFalse();

    /**
     * 根据内容模糊查询
     */
    Page<Comment> findByContentContaining(String content, Pageable pageable);

    /**
     * 根据用户ID查询评论（未删除，分页）
     */
    Page<Comment> findByUserIdAndDeletedFalseOrderByCreateTimeDesc(Long userId, Pageable pageable);
}
