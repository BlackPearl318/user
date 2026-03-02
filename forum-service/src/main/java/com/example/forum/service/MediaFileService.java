package com.example.forum.service;

import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.forum.enums.mediafile.MediaStatus;
import com.example.forum.enums.mediafile.RefType;
import com.example.forum.mapper.MediaFileMapper;
import com.example.forum.pojo.MediaFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaFileService {

    private final MediaFileMapper mediaFileMapper;

    @Autowired
    public MediaFileService(MediaFileMapper mediaFileMapper) {
        this.mediaFileMapper = mediaFileMapper;
    }

    // 帖子和附件绑定
    @Transactional
    public void bindMediaToPost(List<Long> mediaIds, Long tenantId, Long userId, Long postId) {

        List<Long> distinctIds = mediaIds.stream()
                .distinct()
                .toList();

        List<MediaFile> mediaList = mediaFileMapper.selectBatchIds(distinctIds);

        if (mediaList.size() != distinctIds.size()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "部分附件不存在");
        }

        for (MediaFile media : mediaList) {
            if (!media.getTenantId().equals(tenantId)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "非法附件");
            }
            if (!media.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "不能使用他人附件");
            }
            if (media.getStatus() != MediaStatus.NORMAL) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "附件状态异常");
            }
            // 如果已经绑定，禁止重复绑定
            if (media.getRefId() != null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "附件已被绑定");
            }
        }

        // 批量绑定
        mediaFileMapper.batchBindToPost(distinctIds, postId);
    }

    // 解除附件和帖子的绑定
    public void unbindByPost(Long postId) {
        mediaFileMapper.unbindByPost(postId);
    }
}
