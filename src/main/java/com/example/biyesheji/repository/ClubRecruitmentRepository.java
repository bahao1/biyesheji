package com.example.biyesheji.repository;

import com.example.biyesheji.entity.ClubRecruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 社团招新Repository
 */
@Repository
public interface ClubRecruitmentRepository extends JpaRepository<ClubRecruitment, Long> {

    /**
     * 查询未删除的招新信息
     */
    Page<ClubRecruitment> findByDeletedFalse(Pageable pageable);

    /**
     * 根据社团ID查询招新信息
     */
    Page<ClubRecruitment> findByClubIdAndDeletedFalse(Long clubId, Pageable pageable);

    /**
     * 根据标题模糊查询
     */
    Page<ClubRecruitment> findByTitleContainingAndDeletedFalse(String title, Pageable pageable);

    /**
     * 根据状态查询
     */
    Page<ClubRecruitment> findByStatusAndDeletedFalse(String status, Pageable pageable);
}
