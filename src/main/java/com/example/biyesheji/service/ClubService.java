package com.example.biyesheji.service;

import com.example.biyesheji.entity.Club;
import com.example.biyesheji.entity.ClubRecruitment;
import com.example.biyesheji.entity.User;
import com.example.biyesheji.repository.ClubRecruitmentRepository;
import com.example.biyesheji.repository.ClubRepository;
import com.example.biyesheji.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 社团Service
 */
@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final ClubRecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;

    /**
     * 查询所有社团
     */
    public Page<Club> queryClubs(String keyword, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        if (keyword != null && !keyword.isEmpty()) {
            return clubRepository.findByNameContainingAndDeletedFalse(keyword, pageable);
        }
        return clubRepository.findByDeletedFalse(pageable);
    }

    /**
     * 根据社长ID获取社团
     */
    public Club getClubByPresidentId(Long presidentId) {
        return clubRepository.findByPresidentIdAndDeletedFalse(presidentId);
    }

    /**
     * 根据用户获取其管理的社团（通过user.clubId或presidentId）
     */
    public Club getClubByUser(Long userId) {
        // 首先查找用户
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 如果用户有clubId，直接通过clubId获取社团
        if (user.getClubId() != null) {
            return clubRepository.findById(user.getClubId())
                    .orElse(null);
        }

        // 否则通过presidentId查找
        return clubRepository.findByPresidentIdAndDeletedFalse(userId);
    }

    /**
     * 创建社团招新
     */
    @Transactional
    public ClubRecruitment createRecruitment(Long clubId, ClubRecruitment recruitment) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("社团不存在"));

        recruitment.setClubId(clubId);
        recruitment.setClubName(club.getName());
        return recruitmentRepository.save(recruitment);
    }

    /**
     * 查询招新信息
     */
    public Page<ClubRecruitment> queryRecruitments(Long clubId, String keyword, String status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));

        Page<ClubRecruitment> recruitments;
        if (clubId != null) {
            recruitments = recruitmentRepository.findByClubIdAndDeletedFalse(clubId, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            recruitments = recruitmentRepository.findByTitleContainingAndDeletedFalse(keyword, pageable);
        } else if (status != null && !status.isEmpty()) {
            recruitments = recruitmentRepository.findByStatusAndDeletedFalse(status, pageable);
        } else {
            recruitments = recruitmentRepository.findByDeletedFalse(pageable);
        }

        // 为每个招新信息设置社长ID
        recruitments.forEach(recruitment -> {
            Club club = clubRepository.findById(recruitment.getClubId()).orElse(null);
            if (club != null) {
                recruitment.setPresidentId(club.getPresidentId());
            }
        });

        return recruitments;
    }

    /**
     * 获取招新详情
     */
    @Transactional
    public ClubRecruitment getRecruitmentById(Long id) {
        ClubRecruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招新信息不存在"));

        // 增加浏览量
        recruitment.setViewCount(recruitment.getViewCount() + 1);
        recruitmentRepository.save(recruitment);

        // 获取社团的社长ID
        Club club = clubRepository.findById(recruitment.getClubId()).orElse(null);
        if (club != null) {
            recruitment.setPresidentId(club.getPresidentId());
        }

        return recruitment;
    }

    /**
     * 更新招新信息
     */
    @Transactional
    public ClubRecruitment updateRecruitment(Long id, ClubRecruitment updatedRecruitment) {
        ClubRecruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招新信息不存在"));

        recruitment.setTitle(updatedRecruitment.getTitle());
        recruitment.setContent(updatedRecruitment.getContent());
        recruitment.setRequirements(updatedRecruitment.getRequirements());
        recruitment.setQuota(updatedRecruitment.getQuota());
        recruitment.setDeadline(updatedRecruitment.getDeadline());
        recruitment.setContact(updatedRecruitment.getContact());
        recruitment.setStatus(updatedRecruitment.getStatus());

        return recruitmentRepository.save(recruitment);
    }

    /**
     * 删除招新信息
     */
    @Transactional
    public void deleteRecruitment(Long id) {
        ClubRecruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("招新信息不存在"));
        recruitment.setDeleted(true);
        recruitmentRepository.save(recruitment);
    }
}
