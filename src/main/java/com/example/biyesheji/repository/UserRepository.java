package com.example.biyesheji.repository;

import com.example.biyesheji.entity.User;
import com.example.biyesheji.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 判断用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 判断邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 判断手机号是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 统计未禁用的用户数量
     */
    long countByDisabledFalse();

    /**
     * 根据用户名或昵称模糊查询
     */
    Page<User> findByUsernameContainingOrNicknameContaining(String username, String nickname, Pageable pageable);

    /**
     * 根据角色查询
     */
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * 根据用户名或昵称模糊查询且角色匹配
     */
    Page<User> findByUsernameContainingOrNicknameContainingAndRole(String username, String nickname, UserRole role, Pageable pageable);
}
