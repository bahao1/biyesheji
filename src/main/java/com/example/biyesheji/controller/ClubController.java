package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.entity.Club;
import com.example.biyesheji.entity.ClubRecruitment;
import com.example.biyesheji.service.ClubService;
import com.example.biyesheji.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 社团Controller
 */
@Tag(name = "社团管理")
@RestController
@RequestMapping("/club")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "查询社团列表")
    @GetMapping("/query")
    public Result<Page<Club>> queryClubs(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(clubService.queryClubs(keyword, page, pageSize));
    }

    @Operation(summary = "查询招新列表")
    @GetMapping("/recruitment/query")
    public Result<Page<ClubRecruitment>> queryRecruitments(
            @Parameter(description = "社团ID") @RequestParam(required = false) Long clubId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(clubService.queryRecruitments(clubId, keyword, status, page, pageSize));
    }

    @Operation(summary = "获取招新详情")
    @GetMapping("/recruitment/{id}")
    public Result<ClubRecruitment> getRecruitmentById(@PathVariable Long id) {
        return Result.success(clubService.getRecruitmentById(id));
    }

    @Operation(summary = "创建招新信息")
    @PostMapping("/recruitment/create")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public Result<ClubRecruitment> createRecruitment(
            @RequestBody ClubRecruitment recruitment,
            HttpServletRequest request
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Club club = clubService.getClubByUser(userId);
        if (club == null) {
            return Result.error("您不是任何社团的管理员");
        }
        return Result.success(clubService.createRecruitment(club.getId(), recruitment));
    }

    @Operation(summary = "更新招新信息")
    @PutMapping("/recruitment/{id}")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public Result<ClubRecruitment> updateRecruitment(
            @PathVariable Long id,
            @RequestBody ClubRecruitment recruitment,
            HttpServletRequest request
    ) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Club club = clubService.getClubByUser(userId);
        if (club == null) {
            return Result.error("您不是任何社团的管理员");
        }
        return Result.success(clubService.updateRecruitment(id, recruitment));
    }

    @Operation(summary = "删除招新信息")
    @DeleteMapping("/recruitment/{id}")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public Result<Void> deleteRecruitment(@PathVariable Long id, HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Club club = clubService.getClubByUser(userId);
        if (club == null) {
            return Result.error("您不是任何社团的管理员");
        }
        clubService.deleteRecruitment(id);
        return Result.success();
    }

    @Operation(summary = "获取我管理的社团")
    @GetMapping("/my-club")
    @PreAuthorize("hasRole('CLUB_ADMIN')")
    public Result<Club> getMyClub(HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Club club = clubService.getClubByUser(userId);
        if (club == null) {
            return Result.error("您不是任何社团的管理员");
        }
        return Result.success(club);
    }
}
