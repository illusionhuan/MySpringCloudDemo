package com.example.content.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.exception.ResourceNotFoundException;
import com.example.content.dto.ContentDTO;
import com.example.content.dto.UserDTO;
import com.example.content.entity.Content;
import com.example.content.feign.UserFeignClient;
import com.example.content.repository.ContentMapper;
import com.example.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子业务逻辑层
 *
 * @author demo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentMapper contentMapper;
    private final UserFeignClient userFeignClient;

    /**
     * 发布帖子
     *
     * @param content 帖子信息
     * @return 创建后的帖子 DTO
     */
    @Transactional
    public ContentDTO createContent(Content content) {
        log.info("发布帖子: {}", content.getTitle());

        // Feign 调用验证用户存在
        ApiResponse<UserDTO> userResp = userFeignClient.getUserById(content.getUserId());
        String authorName = (userResp.getData() != null) ? userResp.getData().getName() : "未知用户";

        content.setCreatedAt(LocalDateTime.now());
        content.setUpdatedAt(LocalDateTime.now());
        contentMapper.insert(content);

        return toDTO(content, authorName);
    }

    /**
     * 查询帖子列表
     *
     * @return 帖子列表
     */
    public List<Content> findAll() {
        log.debug("查询所有帖子");
        return contentMapper.selectList(
                new LambdaQueryWrapper<Content>()
                        .eq(Content::getStatus, "PUBLISHED")
                        .orderByDesc(Content::getCreatedAt));
    }

    /**
     * 根据 ID 查询帖子
     *
     * @param id 帖子 ID
     * @return 帖子信息
     */
    public Content findById(Long id) {
        log.debug("查询帖子: {}", id);
        Content content = contentMapper.selectById(id);
        if (content == null) {
            throw new ResourceNotFoundException("Content", "id", id);
        }
        return content;
    }

    /**
     * 查询某用户的帖子
     *
     * @param userId 用户 ID
     * @return 帖子列表
     */
    public List<Content> findByUserId(Long userId) {
        log.debug("查询用户帖子: {}", userId);
        return contentMapper.selectList(
                new LambdaQueryWrapper<Content>()
                        .eq(Content::getUserId, userId)
                        .orderByDesc(Content::getCreatedAt));
    }

    /**
     * 更新帖子
     *
     * @param id      帖子 ID
     * @param updated 更新信息
     * @return 更新后的帖子
     */
    @Transactional
    public Content updateContent(Long id, Content updated) {
        log.info("更新帖子: {}", id);
        Content content = findById(id);
        content.setTitle(updated.getTitle());
        content.setBody(updated.getBody());
        content.setUpdatedAt(LocalDateTime.now());
        contentMapper.updateById(content);
        return content;
    }

    /**
     * 删除帖子（软删除）
     *
     * @param id 帖子 ID
     */
    @Transactional
    public void deleteContent(Long id) {
        log.info("删除帖子: {}", id);
        Content content = findById(id);
        content.setStatus("DELETED");
        content.setUpdatedAt(LocalDateTime.now());
        contentMapper.updateById(content);
    }

    private ContentDTO toDTO(Content content, String authorName) {
        ContentDTO dto = new ContentDTO();
        dto.setId(content.getId());
        dto.setUserId(content.getUserId());
        dto.setAuthorName(authorName);
        dto.setTitle(content.getTitle());
        dto.setBody(content.getBody());
        dto.setStatus(content.getStatus());
        dto.setCreatedAt(content.getCreatedAt());
        return dto;
    }
}
