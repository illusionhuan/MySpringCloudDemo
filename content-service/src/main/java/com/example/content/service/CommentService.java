package com.example.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.exception.ResourceNotFoundException;
import com.example.content.entity.Comment;
import com.example.content.entity.Content;
import com.example.content.event.CommentEventProducer;
import com.example.content.repository.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论业务逻辑层
 *
 * @author demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;
    private final ContentService contentService;
    private final CommentEventProducer eventProducer;

    /**
     * 新增评论
     *
     * @param comment 评论信息
     * @return 创建后的评论
     */
    @Transactional
    public Comment createComment(Comment comment) {
        log.info("新增评论, contentId: {}", comment.getContentId());

        Content content = contentService.findById(comment.getContentId());

        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        eventProducer.sendCommentAddedEvent(comment.getId(), comment.getContentId(), comment.getUserId(), content.getUserId());

        return comment;
    }

    /**
     * 查询某帖子的评论
     *
     * @param contentId 帖子 ID
     * @return 评论列表
     */
    public List<Comment> findByContentId(Long contentId) {
        log.debug("查询帖子评论: {}", contentId);
        return commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getContentId, contentId)
                        .orderByAsc(Comment::getCreatedAt));
    }

    /**
     * 删除评论
     *
     * @param id 评论 ID
     */
    @Transactional
    public void deleteComment(Long id) {
        log.info("删除评论: {}", id);
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new ResourceNotFoundException("Comment", "id", id);
        }
        commentMapper.deleteById(id);
    }
}
