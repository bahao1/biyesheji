package com.example.biyesheji.service;

import cn.hutool.core.bean.BeanUtil;
import com.example.biyesheji.dto.LoginRequest;
import com.example.biyesheji.dto.LoginResponse;
import com.example.biyesheji.dto.RegisterRequest;
import com.example.biyesheji.dto.UserDTO;
import com.example.biyesheji.entity.User;
import com.example.biyesheji.enums.UserRole;
import com.example.biyesheji.exception.BusinessException;
import com.example.biyesheji.repository.UserRepository;
import com.example.biyesheji.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @Transactional
    public UserDTO register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 检查手机号是否已存在
        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        user = userRepository.save(user);
        log.info("用户注册成功: {}", user.getUsername());

        return convertToDTO(user);
    }

    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查账号是否被禁用
        if (!user.getEnabled()) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        log.info("用户登录成功: ", user.getUsername());

        return new LoginResponse(token, convertToDTO(user));
    }

    /**
     * 根据ID获取用户信息
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return convertToDTO(user);
    }

    /**
     * 根据用户名获取用户信息
     */
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return convertToDTO(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 更新允许修改的字段
        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }
        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }

        user = userRepository.save(user);
        log.info("用户信息更新成功: {}", user.getUsername());

        return convertToDTO(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 验证旧密码是否正确
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 验证新密码不能为空
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BusinessException("新密码不能为空");
        }

        // 验证新密码长度
        if (newPassword.length() < 6) {
            throw new BusinessException("新密码长度不能少于6位");
        }

        // 加密并保存新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("用户 {} 修改密码成功", user.getUsername());
    }

    /**
     * 实体转DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtil.copyProperties(user, dto);
        return dto;
    }
}
