package com.example.biyesheji.repository;

import com.example.biyesheji.entity.Post;
import com.example.biyesheji.enums.PostCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 帖子数据访问层
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 根据分类查询帖子（未删除）
     */
    Page<Post> findByCategoryAndDeletedFalseOrderByIsTopDescCreateTimeDesc(PostCategory category, Pageable pageable);

    /**
     * 查询所有帖子（未删除）
     */
    Page<Post> findByDeletedFalseOrderByIsTopDescCreateTimeDesc(Pageable pageable);

    /**
     * 根据用户ID查询帖子(未删除)
     */
    Page<Post> findByUserIdAndDeletedFalseOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据用户ID查询帖子(未删除) - 简化版
     */
    Page<Post> findByUserIdAndDeletedFalse(Long userId, Pageable pageable);

    /**
     * 关键词搜索（标题或内容）
     */
    @Query("SELECT p FROM Post p WHERE p.deleted = false AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY p.isTop DESC, p.createTime DESC")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 增加浏览次数
     */
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * 增加点赞数
     */
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞数
     */
    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :id AND p.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);

    /**
     * 增加评论数
     */
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1 WHERE p.id = :id")
    void incrementCommentCount(@Param("id") Long id);

    /**
     * 减少评论数
     */
    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount - 1 WHERE p.id = :id AND p.commentCount > 0")
    void decrementCommentCount(@Param("id") Long id);

    /**
     * 统计未删除的帖子数量
     */
    long countByDeletedFalse();

    /**
     * 根据标题或内容模糊查询
     */
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
