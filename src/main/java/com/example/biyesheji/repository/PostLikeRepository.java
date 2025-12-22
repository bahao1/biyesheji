package com.example.biyesheji.repository;

import com.example.biyesheji.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 帖子点赞数据访问层
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 查询用户是否点赞了某个帖子
     */
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * 判断用户是否点赞了某个帖子
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /**
     * 删除点赞记录
     */
    void deleteByPostIdAndUserId(Long postId, Long userId);

    /**
     * 查询用户点赞的所有帖子ID
     */
    List<PostLike> findByUserId(Long userId);
}
