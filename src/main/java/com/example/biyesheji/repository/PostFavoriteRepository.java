package com.example.biyesheji.repository;

import com.example.biyesheji.entity.PostFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 帖子收藏数据访问层
 */
@Repository
public interface PostFavoriteRepository extends JpaRepository<PostFavorite, Long> {

    /**
     * 查询用户是否收藏了某个帖子
     */
    Optional<PostFavorite> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * 判断用户是否收藏了某个帖子
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /**
     * 删除收藏记录
     */
    void deleteByPostIdAndUserId(Long postId, Long userId);

    /**
     * 查询用户收藏的帖子（分页）
     */
    @Query("SELECT pf.postId FROM PostFavorite pf WHERE pf.userId = :userId ORDER BY pf.createTime DESC")
    Page<Long> findPostIdsByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 查询用户收藏的所有帖子ID
     */
    List<PostFavorite> findByUserId(Long userId);
}
