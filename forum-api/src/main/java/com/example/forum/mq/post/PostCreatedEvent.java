package com.example.forum.mq.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 帖子创建事件
 * 用于通知搜索服务或当前服务异步同步 Elasticsearch 索引
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreatedEvent {

    /**
     * 帖子ID
     */
    private Long postId;
}
