package com.example.forum.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.forum.dto.PostCreateDTO;
import com.example.forum.enums.normal.YesNoStatus;
import com.example.forum.enums.post.PostStatus;
import com.example.forum.mapper.PostMapper;
import com.example.forum.mq.post.MQConstants;
import com.example.forum.mq.post.PostCreatedEvent;
import com.example.forum.pojo.Post;
import com.example.forum.vo.PageResult;
import com.example.forum.vo.PostVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
public class PostService {

    private final PostMapper postMapper;

    private final MediaFileService mediaFileService;
    private final CommentService commentService;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public PostService(PostMapper postMapper,
                       MediaFileService mediaFileService,
                       CommentService commentService, RabbitTemplate rabbitTemplate) {
        this.postMapper = postMapper;
        this.mediaFileService = mediaFileService;
        this.commentService = commentService;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 发布帖子
     *
     * 业务流程：
     * 1. 保存 MySQL 数据
     * 2. 绑定附件
     * 3. 事务提交后发送 MQ，同步 ES
     */
    @Transactional
    public void uploadPost(PostCreateDTO dto, Long tenantId, Long userId) {

        // 构建帖子实体
        Post post = Post.builder()
                .tenantId(tenantId)
                .userId(userId)
                .categoryId(dto.getCategoryId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .summary(dto.getSummary())
                .status(PostStatus.NORMAL)
                .isTop(YesNoStatus.NO)
                .isEssence(YesNoStatus.NO)
                .viewCount(0L)
                .likeCount(0L)
                .commentCount(0L)
                .build();

        postMapper.insert(post);

        Long postId = post.getId();

        // 绑定附件
        if (CollectionUtils.isNotEmpty(dto.getMediaIds())) {
            mediaFileService.bindMediaToPost(
                    dto.getMediaIds(),
                    tenantId,
                    userId,
                    postId
            );
        }

        // 事务提交后发送 MQ
        // 异步保证mysql和elasticsearch数据一致性
        if (TransactionSynchronizationManager.isActualTransactionActive()) {

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {

                            PostCreatedEvent event = new PostCreatedEvent(postId);
                            rabbitTemplate.convertAndSend(
                                    MQConstants.POST_EXCHANGE,
                                    MQConstants.POST_CREATED_KEY,
                                    event
                            );
                        }
                    }
            );
        }
    }

    // 删除帖子
    @Transactional
    public void deletePost(Long postId, Long tenantId, Long userId) {

        // 查询
        Post post = postMapper.selectById(postId);
        if (post == null || !post.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 权限校验（作者删除）
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 状态校验
        if (post.getStatus() == PostStatus.DELETED) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "帖子已删除");
        }

        // 逻辑删除帖子
        int affected = postMapper.logicDelete(postId);
        if (affected != 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败");
        }

        // 解绑附件
        mediaFileService.unbindByPost(postId);

        // 逻辑删除评论
        commentService.deleteByPost(postId);
    }

    /**
     *  获取所有帖子
     *
     * @param pageNum  当前页
     * @param pageSize 每页条数
     */
    public PageResult<PostVO> listAllPosts(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        List<Post> posts = postMapper.selectAllPosts();

        PageInfo<Post> pageInfo = new PageInfo<>(posts);

        List<PostVO> voList = posts.stream()
                .map(this::convertToVO)
                .toList();

        PageInfo<PostVO> voPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, voPageInfo);
        voPageInfo.setList(voList);

        return new PageResult<>(voPageInfo);
    }

    /**
     * 根据 tenantId 分页查询帖子
     *
     * @param tenantId 租户ID
     * @param pageNum  当前页
     * @param pageSize 每页条数
     */
    public PageResult<PostVO> listPostsByTenantId(Long tenantId, int pageNum, int pageSize) {

        // 启动分页
        PageHelper.startPage(pageNum, pageSize);

        // 执行查询
        List<Post> posts = postMapper.selectPostsByTenantId(tenantId);

        // 封装分页信息
        PageInfo<Post> pageInfo = new PageInfo<>(posts);

        // 转换为 VO
        List<PostVO> voList = posts.stream()
                .map(this::convertToVO)
                .toList();

        // 构建新的 PageInfo 用于返回
        PageInfo<PostVO> voPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, voPageInfo);
        voPageInfo.setList(voList);

        return new PageResult<>(voPageInfo);
    }

    /**
     * 根据 userId 分页查询帖子
     *
     * @param userId   用户ID
     * @param pageNum  当前页
     * @param pageSize 每页条数
     */
    public PageResult<PostVO> listPostsByUserId(Long userId,
                                                int pageNum,
                                                int pageSize) {

        // 启动分页
        PageHelper.startPage(pageNum, pageSize);

        // 执行查询
        List<Post> posts = postMapper.selectPostsByUserId(userId);

        // 构建分页信息
        PageInfo<Post> pageInfo = new PageInfo<>(posts);

        // 转换为 VO
        List<PostVO> voList = posts.stream()
                .map(this::convertToVO)
                .toList();

        // 构建 VO 分页对象
        PageInfo<PostVO> voPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, voPageInfo);
        voPageInfo.setList(voList);

        return new PageResult<>(voPageInfo);
    }

    /**
     * DO -> VO 转换
     */
    private PostVO convertToVO(Post post) {
        PostVO vo = new PostVO();
        BeanUtils.copyProperties(post, vo);
        return vo;
    }



}
