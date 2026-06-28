package com.example.content.controller;

import com.example.common.dto.ApiResponse;
import com.example.content.entity.Comment;
import com.example.content.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理控制器
 *
 * @author demo
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 新增评论
     *
     * @param comment 评论信息
     * @return 创建后的评论
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Comment> create(@RequestBody Comment comment) {
        return ApiResponse.success(commentService.createComment(comment));
    }

    /**
     * 查询某帖子的评论
     *
     * @param contentId 帖子 ID
     * @return 评论列表
     */
    @GetMapping("/content/{contentId}")
    public ApiResponse<List<Comment>> getByContentId(@PathVariable Long contentId) {
        return ApiResponse.success(commentService.findByContentId(contentId));
    }

    /**
     * 删除评论
     *
     * @param id 评论 ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}
