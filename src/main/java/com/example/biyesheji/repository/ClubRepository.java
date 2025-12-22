package com.example.biyesheji.repository;

import com.example.biyesheji.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 社团Repository
 */
@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    /**
     * 查询未删除的社团
     */
    Page<Club> findByDeletedFalse(Pageable pageable);

    /**
     * 根据社团名称模糊查询
     */
    Page<Club> findByNameContainingAndDeletedFalse(String name, Pageable pageable);

    /**
     * 根据社长ID查询社团
     */
    Club findByPresidentIdAndDeletedFalse(Long presidentId);

    /**
     * 统计未删除的社团数量
     */
    long countByDeletedFalse();
}
