package com.example.biyesheji.controller;

import com.example.biyesheji.common.Result;
import com.example.biyesheji.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传Controller
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileService.uploadFile(file);
        return Result.success("上传成功", fileUrl);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER', 'CLUB_ADMIN', 'SUPER_ADMIN')")
    public Result<Void> deleteFile(@RequestParam("url") String fileUrl) {
        fileService.deleteFile(fileUrl);
        return Result.success();
    }
}
