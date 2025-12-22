package com.example.biyesheji.repository;

import com.example.biyesheji.entity.Carpool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 拼车Repository
 */
@Repository
public interface CarpoolRepository extends JpaRepository<Carpool, Long> {

    /**
     * 查询所有拼车信息(未删除)
     */
    Page<Carpool> findByDeletedFalseOrderByCreateTimeDesc(Pageable pageable);

    /**
     * 根据类型查询(未删除)
     */
    Page<Carpool> findByTypeAndDeletedFalseOrderByCreateTimeDesc(String type, Pageable pageable);

    /**
     * 根据用户ID查询(未删除)
     */
    Page<Carpool> findByUserIdAndDeletedFalseOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 关键词搜索(出发地或目的地)
     */
    @Query("SELECT c FROM Carpool c WHERE c.deleted = false AND (c.departurePlace LIKE %:keyword% OR c.destination LIKE %:keyword%) ORDER BY c.createTime DESC")
    Page<Carpool> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 增加浏览次数
     */
    @Modifying
    @Query("UPDATE Carpool c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
