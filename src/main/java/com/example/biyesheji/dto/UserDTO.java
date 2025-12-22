package com.example.biyesheji.dto;

import com.example.biyesheji.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息DTO（返回给前端，不包含密码）
 */
@Data
public class UserDTO {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private UserRole role;
    private Long clubId;
    private Boolean enabled;
    private LocalDateTime createTime;
}
