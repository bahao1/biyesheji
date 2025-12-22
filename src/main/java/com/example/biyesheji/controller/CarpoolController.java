package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.entity.Carpool;
import com.example.biyesheji.service.CarpoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 拼车Controller
 */
@Tag(name = "拼车管理")
@RestController
@RequestMapping("/carpool")
@RequiredArgsConstructor
public class CarpoolController {

    private final CarpoolService carpoolService;

    @Operation(summary = "创建拼车信息")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Carpool> createCarpool(@RequestBody Carpool carpool, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Carpool created = carpoolService.createCarpool(userId, carpool);
        return Result.success(created);
    }

    @Operation(summary = "更新拼车信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Carpool> updateCarpool(
            @PathVariable Long id,
            @RequestBody Carpool carpool,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Carpool updated = carpoolService.updateCarpool(id, userId, carpool);
        return Result.success(updated);
    }

    @Operation(summary = "删除拼车信息")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> deleteCarpool(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        carpoolService.deleteCarpool(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取拼车详情")
    @GetMapping("/{id}")
    public Result<Carpool> getCarpoolById(@PathVariable Long id) {
        Carpool carpool = carpoolService.getCarpoolById(id);
        return Result.success(carpool);
    }

    @Operation(summary = "查询拼车列表")
    @GetMapping("/query")
    public Result<Page<Carpool>> queryCarpools(
            @Parameter(description = "类型") @RequestParam(required = false) String type,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize
    ) {
        Page<Carpool> carpools = carpoolService.queryCarpools(type, keyword, page, pageSize);
        return Result.success(carpools);
    }

    @Operation(summary = "获取我的拼车列表")
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Page<Carpool>> getMyCarpools(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Page<Carpool> carpools = carpoolService.getUserCarpools(userId, page, pageSize);
        return Result.success(carpools);
    }

    @Operation(summary = "参与拼车")
    @PostMapping("/{id}/join")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Carpool> joinCarpool(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Carpool carpool = carpoolService.joinCarpool(id, userId);
        return Result.success(carpool);
    }

    @Operation(summary = "取消参与拼车")
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Carpool> cancelJoinCarpool(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Carpool carpool = carpoolService.cancelJoinCarpool(id, userId);
        return Result.success(carpool);
    }
}
