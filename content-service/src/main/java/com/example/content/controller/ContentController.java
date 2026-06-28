package com.example.content.controller;

import com.example.common.dto.ApiResponse;
import com.example.content.dto.ContentDTO;
import com.example.content.entity.Content;
import com.example.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 帖子管理控制器
 *
 * @author demo
 */
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ContentDTO> create(@RequestBody Content content) {
        return ApiResponse.success(contentService.createContent(content));
    }

    @GetMapping
    public ApiResponse<List<Content>> list() {
        return ApiResponse.success(contentService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Content> getById(@PathVariable Long id) {
        return ApiResponse.success(contentService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Content>> getByUserId(@PathVariable Long userId) {
        return ApiResponse.success(contentService.findByUserId(userId));
    }

    @PutMapping("/{id}")
    public ApiResponse<Content> update(@PathVariable Long id, @RequestBody Content content) {
        return ApiResponse.success(contentService.updateContent(id, content));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        contentService.deleteContent(id);
    }
}
