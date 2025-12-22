package com.example.biyesheji.service;

import com.example.biyesheji.entity.Carpool;
import com.example.biyesheji.exception.BusinessException;
import com.example.biyesheji.repository.CarpoolRepository;
import com.example.biyesheji.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 拼车Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CarpoolService {

    private final CarpoolRepository carpoolRepository;
    private final UserRepository userRepository;

    /**
     * 创建拼车信息
     */
    @Transactional
    public Carpool createCarpool(Long userId, Carpool carpool) {
        // 验证用户是否存在
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        carpool.setUserId(userId);
        // 初始化已参与人数为0（还没有人参与）
        carpool.setRemainingSeats(0);
        carpool.setStatus("OPEN");
        carpool.setViewCount(0);
        carpool.setDeleted(false);

        Carpool saved = carpoolRepository.save(carpool);
        log.info("用户 {} 创建拼车信息 {}", userId, saved.getId());
        return saved;
    }

    /**
     * 更新拼车信息
     */
    @Transactional
    public Carpool updateCarpool(Long id, Long userId, Carpool carpool) {
        Carpool existing = carpoolRepository.findById(id)
                .orElseThrow(() -> new BusinessException("拼车信息不存在"));

        if (existing.getDeleted()) {
            throw new BusinessException("拼车信息已被删除");
        }

        // 只能修改自己发布的
        if (!existing.getUserId().equals(userId)) {
            throw new BusinessException("只能修改自己发布的拼车信息");
        }

        existing.setDeparturePlace(carpool.getDeparturePlace());
        existing.setDestination(carpool.getDestination());
        existing.setDepartureTime(carpool.getDepartureTime());
        existing.setSeats(carpool.getSeats());
        existing.setPricePerPerson(carpool.getPricePerPerson());
        existing.setContact(carpool.getContact());
        existing.setRemarks(carpool.getRemarks());
        existing.setStatus(carpool.getStatus());

        Carpool updated = carpoolRepository.save(existing);
        log.info("用户 {} 更新拼车信息 {}", userId, id);
        return updated;
    }

    /**
     * 删除拼车信息
     */
    @Transactional
    public void deleteCarpool(Long id, Long userId) {
        Carpool carpool = carpoolRepository.findById(id)
                .orElseThrow(() -> new BusinessException("拼车信息不存在"));

        // 只能删除自己发布的
        if (!carpool.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己发布的拼车信息");
        }

        carpool.setDeleted(true);
        carpoolRepository.save(carpool);
        log.info("用户 {} 删除拼车信息 {}", userId, id);
    }

    /**
     * 获取拼车详情
     */
    @Transactional
    public Carpool getCarpoolById(Long id) {
        Carpool carpool = carpoolRepository.findById(id)
                .orElseThrow(() -> new BusinessException("拼车信息不存在"));

        if (carpool.getDeleted()) {
            throw new BusinessException("拼车信息已被删除");
        }

        // 增加浏览次数
        carpoolRepository.incrementViewCount(id);

        return carpool;
    }

    /**
     * 查询拼车列表
     */
    public Page<Carpool> queryCarpools(String type, String keyword, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        if (keyword != null && !keyword.trim().isEmpty()) {
            return carpoolRepository.searchByKeyword(keyword, pageable);
        }

        if (type != null && !type.trim().isEmpty()) {
            return carpoolRepository.findByTypeAndDeletedFalseOrderByCreateTimeDesc(type, pageable);
        }

        return carpoolRepository.findByDeletedFalseOrderByCreateTimeDesc(pageable);
    }

    /**
     * 获取用户发布的拼车列表
     */
    public Page<Carpool> getUserCarpools(Long userId, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return carpoolRepository.findByUserIdAndDeletedFalseOrderByCreateTimeDesc(userId, pageable);
    }

    /**
     * 参与拼车
     */
    @Transactional
    public Carpool joinCarpool(Long carpoolId, Long userId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new BusinessException("拼车信息不存在"));

        if (carpool.getDeleted()) {
            throw new BusinessException("拼车信息已被删除");
        }

        if (!carpool.getStatus().equals("OPEN")) {
            throw new BusinessException("该拼车已关闭");
        }

        // 检查是否已满
        if (carpool.getRemainingSeats() >= carpool.getSeats()) {
            throw new BusinessException("拼车人数已满");
        }

        // 不能参与自己发布的拼车
        if (carpool.getUserId().equals(userId)) {
            throw new BusinessException("不能参与自己发布的拼车");
        }

        // 增加已参与人数
        carpool.setRemainingSeats(carpool.getRemainingSeats() + 1);

        // 如果满了，自动改为FULL状态
        if (carpool.getRemainingSeats() >= carpool.getSeats()) {
            carpool.setStatus("FULL");
        }

        Carpool updated = carpoolRepository.save(carpool);
        log.info("用户 {} 参与拼车 {}", userId, carpoolId);
        return updated;
    }

    /**
     * 取消参与拼车
     */
    @Transactional
    public Carpool cancelJoinCarpool(Long carpoolId, Long userId) {
        Carpool carpool = carpoolRepository.findById(carpoolId)
                .orElseThrow(() -> new BusinessException("拼车信息不存在"));

        if (carpool.getDeleted()) {
            throw new BusinessException("拼车信息已被删除");
        }

        if (carpool.getRemainingSeats() <= 0) {
            throw new BusinessException("当前没有参与人数，无法取消");
        }

        // 减少已参与人数
        carpool.setRemainingSeats(carpool.getRemainingSeats() - 1);

        // 如果之前是满的，现在有空位了，改为OPEN
        if (carpool.getStatus().equals("FULL")) {
            carpool.setStatus("OPEN");
        }

        Carpool updated = carpoolRepository.save(carpool);
        log.info("用户 {} 取消参与拼车 {}", userId, carpoolId);
        return updated;
    }
}
