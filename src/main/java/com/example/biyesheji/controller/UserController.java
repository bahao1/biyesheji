package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.dto.UserDTO;
import com.example.biyesheji.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public Result<UserDTO> getCurrentUser(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserDTO user = userService.getUserById(userId);
        return Result.success(user);
    }

    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        // 只能修改自己的信息
        if (!currentUserId.equals(id)) {
            return Result.error(403, "只能修改自己的信息");
        }

        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return Result.success("更新成功", updatedUser);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @RequestBody PasswordChangeRequest request,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.successWithMessage("密码修改成功");
    }

    @Operation(summary = "测试普通用户权限")
    @GetMapping("/test/user")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> testUser() {
        return Result.successWithMessage("普通用户权限测试通过");
    }

    @Operation(summary = "测试社团管理员权限")
    @GetMapping("/test/club-admin")
    @PreAuthorize("hasAnyRole('CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> testClubAdmin() {
        return Result.successWithMessage("社团管理员权限测试通过");
    }

    @Operation(summary = "测试超级管理员权限")
    @GetMapping("/test/super-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<Void> testSuperAdmin() {
        return Result.successWithMessage("超级管理员权限测试通过");
    }

    /**
     * 修改密码请求
     */
    @Data
    public static class PasswordChangeRequest {
        private String oldPassword;
        private String newPassword;
    }
}
